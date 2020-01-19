package io.kotest.runner.jvm.spec

import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.materializeRootTests
import io.kotest.core.test.*
import io.kotest.fp.Try
import io.kotest.runner.jvm.TestEngineListener
import io.kotest.runner.jvm.TestExecutor
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [SpecRunner] that executes each [TestCase] in a fresh instance
 * of the [SpecConfiguration] class.
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
class InstancePerTestSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

   private val logger = LoggerFactory.getLogger(this.javaClass)

   // a test may already have been discovered because we re-run the parents each time for a nested test
   // therefore this set is used to avoid repeated discoveries
   private val executed = mutableSetOf<Description>()

   private val results = mutableMapOf<TestCase, TestResult>()

   /**
    * The intention of this runner is that each [TestCase] executes in it's own instance
    * of the containing [SpecConfiguration] class. Therefore, when we begin executing a test case from
    * the queue, we must first instantiate a new spec, and begin execution on _that_ instance.
    *
    * As test lambdas are executed, nested test cases will be registered, these should be ignored
    * if they are not an ancestor of the target. If they are then we can step into them, and
    * continue recursively until we find the target.
    *
    * Once the target is found it can be executed as normal, and any test lambdas it contains
    * can be registered back with the stack for execution later.
    */
   override suspend fun execute(spec: SpecConfiguration): Try<Map<TestCase, TestResult>> = Try {
      spec.materializeRootTests().forEach { executeInCleanSpec(it.testCase) }
      results
   }

   /**
    * The intention of this runner is that each [TestCase] executes in it's own instance
    * of the containing [SpecConfiguration] class. Therefore, when we begin executing a test case from
    * the queue, we must first instantiate a new spec, and begin execution on _that_ instance.
    *
    * As test lambdas are executed, nested test cases will be registered, these should be ignored
    * if they are not an ancestor of the target. If they are then we can step into them, and
    * continue recursively until we find the target.
    *
    * Once the target is found it can be executed as normal, and any test lambdas it contains
    * can be registered back with the stack for execution later.
    */
   private suspend fun executeInCleanSpec(test: TestCase) {
      createInstance(test.spec::class).map { spec ->
         logger.trace("Created new spec instance $spec")
         interceptSpec(spec) {
            // we need to find the same root test but in the newly created spec
            val root = spec.materializeRootTests().first { it.testCase.description.isOnPath(test.description) }
            logger.trace("Starting root test ${root.testCase.description} in search of ${test.description}")
            run(root.testCase, test)
         }
      }
   }

   private suspend fun run(test: TestCase, target: TestCase) {
      val isTarget = test.description == target.description
      coroutineScope {
         val context = object : TestContext() {
            override val testCase: TestCase = test
            override val coroutineContext: CoroutineContext = this@coroutineScope.coroutineContext
            override suspend fun registerTestCase(nested: NestedTest) {

               // check for duplicate names in the same scope
               val namesInScope = mutableSetOf<String>()
               if (namesInScope.contains(nested.name))
                  throw IllegalStateException("Cannot add duplicate test ${nested.name}")
               namesInScope.add(nested.name)

               val t = nested.toTestCase(test.spec, test.description)
               // if we are currently executing the target, then any registered tests are new, and we
               // should begin execution of them in fresh specs
               // otherwise if the test is on the path we can continue in the same spec
               if (isTarget) {
                  executeInCleanSpec(t)
               } else if (t.description.isOnPath(target.description)) {
                  run(t, target)
               }
            }
         }
         val testExecutor = TestExecutor(listener)
         testExecutor.execute(test, context, isTarget) { result ->
            results[test] = result
         }
      }
   }
}
