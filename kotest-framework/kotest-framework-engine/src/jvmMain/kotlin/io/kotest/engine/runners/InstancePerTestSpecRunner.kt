package io.kotest.engine.runners

import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.toTestCase
import io.kotest.core.spec.Spec
import io.kotest.engine.spec.SpecRunner
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.log
import io.kotest.engine.ExecutorExecutionContext
import io.kotest.core.test.TestCaseExecutionListener
import io.kotest.core.internal.TestCaseExecutor
import io.kotest.core.spec.invokeAfterSpec
import io.kotest.core.spec.invokeBeforeSpec
import io.kotest.core.internal.resolvedThreads
import io.kotest.core.test.*
import io.kotest.core.spec.materializeAndOrderRootTests
import io.kotest.engine.toTestResult
import io.kotest.fp.Try
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

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
internal class InstancePerTestSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

   private val results = ConcurrentHashMap<TestCase, TestResult>()

   /**
    * The intention of this runner is that each [TestCase] executes in it's own instance
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
   override suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>> =
      Try {
         runParallel(spec.resolvedThreads(), spec.materializeAndOrderRootTests().map { it.testCase }) {
            executeInCleanSpec(it)
               .getOrThrow()
         }
         results
      }

   /**
    * The intention of this runner is that each [TestCase] executes in it's own instance
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
   private suspend fun executeInCleanSpec(test: TestCase): Try<Spec> {
      return createInstance(test.spec::class)
         .flatMap { it.invokeBeforeSpec() }
         .flatMap { interceptAndRun(it, test) }
         .flatMap { it.invokeAfterSpec() }
   }

   private suspend fun interceptAndRun(spec: Spec, test: TestCase): Try<Spec> = Try {
      log("Created new spec instance $spec")
      // we need to find the same root test but in the newly created spec
      val root = spec.materializeAndOrderRootTests().first { it.testCase.description.isOnPath(test.description) }
      log("Starting root test ${root.testCase.description} in search of ${test.description}")
      run(root.testCase, test)
      spec
   }

   private suspend fun run(test: TestCase, target: TestCase) {
      val isTarget = test.description == target.description
      coroutineScope {
         val context = object : TestContext {
            // check for duplicate names in the same scope
            val namesInScope = mutableSetOf<DescriptionName.TestName>()
            override val testCase: TestCase = test
            override val coroutineContext: CoroutineContext = this@coroutineScope.coroutineContext
            override suspend fun registerTestCase(nested: NestedTest) {

               if (!namesInScope.add(nested.name))
                  throw DuplicatedTestNameException(nested.name)

               val t = nested.toTestCase(test.spec, test.description)
               // if we are currently executing the target, then any registered tests are new, and we
               // should begin execution of them in fresh specs
               // otherwise if the test is on the path we can continue in the same spec
               if (isTarget) {
                  executeInCleanSpec(t).getOrThrow()
               } else if (t.description.isOnPath(target.description)) {
                  run(t, target)
               }
            }
         }
         val testExecutor = TestCaseExecutor(object : TestCaseExecutionListener {
            override fun testStarted(testCase: TestCase) {
               if (isTarget) listener.testStarted(testCase)
            }

            override fun testIgnored(testCase: TestCase) {
               if (isTarget) listener.testIgnored(testCase, null)
            }

            override fun testFinished(testCase: TestCase, result: TestResult) {
               if (isTarget) listener.testFinished(testCase, result)
            }
         }, ExecutorExecutionContext, {}, { t, duration -> toTestResult(t, duration) })

         val result = testExecutor.execute(test, context)
         results[test] = result
      }
   }
}
