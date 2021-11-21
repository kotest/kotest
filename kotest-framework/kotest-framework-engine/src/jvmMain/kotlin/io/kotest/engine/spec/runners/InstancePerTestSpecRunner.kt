package io.kotest.engine.spec.runners

import io.kotest.common.ExperimentalKotest
import io.kotest.common.flatMap
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.spec.Registration
import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.SpecRunner
import io.kotest.engine.test.NoopTestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.engine.test.registration.DuplicateNameHandlingRegistration
import io.kotest.engine.test.scheduler.TestScheduler
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap

/**
 * Implementation of [SpecRunner] that executes each [TestCase] in a fresh instance
 * of the [Spec] class.
 *
 * This differs from the [InstancePerLeafSpecRunner] in that
 * every single test, whether of type [TestType.Test] or [TestType.Container], will be
 * executed separately. Branch tests will ultimately be executed once as a standalone
 * test, and also as part of the "path" to any nested tests.
 *
 * So, given the following structure:
 *
 *  outerTest {
 *    innerTestA {
 *      // test
 *    }
 *    innerTestB {
 *      // test
 *    }
 *  }
 *
 * Three spec instances will be created. The execution process will be:
 *
 * spec1 = instantiate spec
 * spec1.outerTest
 * spec2 = instantiate spec
 * spec2.outerTest
 * spec2.innerTestA
 * spec3 = instantiate spec
 * spec3.outerTest
 * spec3.innerTestB
 */
@ExperimentalKotest
internal class InstancePerTestSpecRunner(
   listener: TestEngineListener,
   schedule: TestScheduler,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val configuration: Configuration,
) : SpecRunner(listener, schedule, configuration) {

   private val logger = Logger(this::class)
   private val extensions = SpecExtensions(configuration.registry())
   private val results = ConcurrentHashMap<TestCase, TestResult>()

   /**
    * The intention of this runner is that each [TestCase] executes in its own instance
    * of the containing [Spec] class. Therefore, when we begin executing a test case from
    * the queue, we must first instantiate a new spec, and begin execution on _that_ instance.
    *
    * As test lambdas are executed, nested test cases will be registered, these should be ignored
    * if they are not an ancestor of the target. If they are then we can step into them, and
    * continue recursively until we find the target.
    *
    * Once the target is found it can be executed as normal, and any test lambdas it contains
    * can be registered back with the stack for execution later.
    */
   override suspend fun execute(spec: Spec): Result<Map<TestCase, TestResult>> =
      runCatching {
//         launch(spec) {
//            executeInCleanSpec(it)
//               .getOrThrow()
//         }
         results
      }

   /**
    * The intention of this runner is that each [TestCase] executes in its own instance
    * of the containing [Spec] class. Therefore, when we begin executing a test case from
    * the queue, we must first instantiate a new spec, and begin execution on _that_ instance.
    *
    * As test lambdas are executed, nested test cases will be registered, these should be ignored
    * if they are not an ancestor of the target. If they are then we can step into them, and
    * continue recursively until we find the target.
    *
    * Once the target is found it can be executed as normal, and any test lambdas it contains
    * can be registered back with the stack for execution later.
    */
   private suspend fun executeInCleanSpec(test: TestCase): Result<Spec> {
      return createInstance(test.spec::class)
         .flatMap { spec ->
            runCatching {
               extensions.intercept(spec) {
                  extensions.beforeSpec(spec)
                     .flatMap { run(it, test) }
                     .flatMap { extensions.afterSpec(it) }
               }
            }.map { spec }
         }
   }

   private suspend fun run(spec: Spec, target: TestCase): Result<Spec> = kotlin.runCatching {
      logger.log { Pair(spec::class.bestName(), "Created new spec instance: $spec") }
      // we need to find the same root test but in the newly created spec
      val root = materializer.materialize(spec).first { it.descriptor.isOnPath(target.descriptor) }
      logger.log { Pair(spec::class.bestName(), "Starting root test ${root.descriptor} for ${target.descriptor}") }
      run(root, target)
      spec
   }

   private suspend fun run(test: TestCase, target: TestCase): TestResult {
      return coroutineScope {
         val testExecutor = TestCaseExecutor(
            // we only log events for this test if it is the target, otherwise, those events will have been logged elsewhere
            if (test.descriptor == target.descriptor)
               TestCaseExecutionListenerToTestEngineListenerAdapter(listener)
            else
               NoopTestCaseExecutionListener,
            defaultCoroutineDispatcherFactory,
            configuration,
            // we only care about handling duplicate names if we are the target
            if (test.descriptor == target.descriptor) {
               DuplicateNameHandlingRegistration(
                  test.spec.duplicateTestNameMode ?: configuration.duplicateTestNameMode,
                  registration(target)
               )
            } else registration(target),
         )
         val result = testExecutor.execute(test)
         results[test] = result
         result
      }
   }

   private fun registration(target: TestCase) = object : Registration {
      override suspend fun runNestedTestCase(parent: TestCase, nested: NestedTest): TestResult? {
         val isTarget = parent.descriptor == target.descriptor
         val t = materializer.materialize(nested, parent)
         // If we are currently executing the target, then any discovered tests are definitely new (as we have
         // not previously executed this scope), and so they all need to be deferred to fresh specs.
         // Otherwise, if the test is on the path we can continue in the same spec as we are "en route" to the target.
         // Finally, if the test is neither our target, nor on the path, it can be ignored as it is not relevant
         // to finding the target test.
         return if (isTarget) {
            executeInCleanSpec(t).getOrThrow()
            null
         } else if (t.descriptor.isOnPath(target.descriptor)) {
            run(t, target)
         } else {
            null
         }
      }
   }
}
