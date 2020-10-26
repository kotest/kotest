package io.kotest.engine.runners

import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.toTestCase
import io.kotest.core.spec.Spec
import io.kotest.core.test.DescriptionName
import io.kotest.engine.listener.BufferedTestCaseExcecutionListener
import io.kotest.engine.spec.SpecRunner
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.ExecutorExecutionContext
import io.kotest.core.internal.TestCaseExecutor
import io.kotest.engine.listener.TestCaseListenerToTestEngineListenerAdapter
import io.kotest.core.spec.invokeAfterSpec
import io.kotest.core.spec.invokeBeforeSpec
import io.kotest.core.spec.materializeAndOrderRootTests
import io.kotest.engine.toTestResult
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

/**
 * A [SpecRunner] that executes each leaf [TestCase] in a seperate instance of the
 * containing [Spec] class, and each test inside it's own coroutine.
 *
 * Each root test is launched using a single threaded dispatcher. The user can specify the number
 * of dispatchers to use (round robin between root tests) by setting the threads value in a spec.
 *
 * All nested tests use the same dispatcher as their root test.
 */
internal class ConcurrentInstancePerLeafSpecRunner(
   testEngineListener: TestEngineListener,
   private val threads: Int,
) : SpecRunner(testEngineListener) {

   private val results = ConcurrentHashMap<TestCase, TestResult>()

   private val testCaseListener =
      BufferedTestCaseExcecutionListener(TestCaseListenerToTestEngineListenerAdapter(testEngineListener))

   override suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>> {
      return Try {

         /**
          * Each root test will run in a single threaded dispatcher, and we make [threads] number of dispatchers.
          * This allows thread affinity in a test, so that the same backing thread is used for all the coroutines of
          * a single test path. Otherwise, things like Java's re-entrant lock will fail, as they use the current
          * thread as part of the acquire/release strategy.
          */
         val dispatchers = List(threads) { Executors.newSingleThreadExecutor().asCoroutineDispatcher() }

         coroutineScope {
            spec.materializeAndOrderRootTests().withIndex().forEach { (index, rootTest) ->
               log("InstancePerLeafConcurrentSpecRunner: Launching coroutine for root test [${rootTest.testCase.description.testPath()}]")
               launch(dispatchers[index % threads]) {
                  executeInCleanSpec(rootTest.testCase).getOrThrow()
                  testCaseListener.rootFinished(rootTest.testCase)
               }
            }
         }

         log("InstancePerLeafConcurrentSpecRunner: Root scope has completed, returning ${results.size} test results")
         results
      }
   }

   private suspend fun executeInCleanSpec(target: TestCase): Try<Spec> {
      log("InstancePerLeafConcurrentSpecRunner: Executing target in clean spec")
      return createInstance(target.spec::class)
         .flatMap { it.invokeBeforeSpec() }
         .flatMap { startTest(it, target.description.testNames()) } // drop the spec name
         .flatMap { it.invokeAfterSpec() }
   }

   // we need to find the same root test but in the newly created spec and begin executing that
   private suspend fun startTest(spec: Spec, targets: List<DescriptionName.TestName>): Try<Spec> {
      require(targets.isNotEmpty())
      return Try {
         log("Created new spec instance $spec")
         val root = spec.materializeAndOrderRootTests().first { it.testCase.description.name == targets.first() }
         run(root.testCase, targets.drop(1))
         spec
      }
   }

   /**
    * Executes a given [TestCase], looking for the first component in the passed list of component names.
    * If the first component is located, then execution continues into that. If any other nested test is
    * discovered, and it hasn't yet be seen on previous executions, then a coroutine is launched to
    * execute that test.
    */
   private suspend fun run(test: TestCase, targets: List<DescriptionName.TestName>) {
      coroutineScope {
         val context = object : TestContext {

            // the first discovered test should be executed using the same spec
            val first = AtomicBoolean(true)

            // check for duplicate names in the same scope
            val namesInScope = ConcurrentHashMap.newKeySet<DescriptionName.TestName>()

            override val testCase: TestCase = test
            override val coroutineContext: CoroutineContext = this@coroutineScope.coroutineContext

            override suspend fun registerTestCase(nested: NestedTest) {

               if (!namesInScope.add(nested.name))
                  throw DuplicatedTestNameException(nested.name)

               val t = nested.toTestCase(test.spec, test.description)

               when {
                  // if the nested test is the next entry that we are looking for, we launch straight into that
                  targets.isNotEmpty() && t.description.name == targets.first() -> launch { run(t, targets.drop(1)) }
                  // if we have no target then we are speculatively executing the scope, so the first nested test
                  // can go on the same spec, any others need to go onto seperate specs.
                  targets.isEmpty() -> {
                     if (first.compareAndSet(true, false)) {
                        launch { run(t, emptyList()) }
                     } else {
                        launch { executeInCleanSpec(t) }
                     }
                  }
               }
            }
         }

         val testExecutor = TestCaseExecutor(
            testCaseListener,
            ExecutorExecutionContext,
            {},
            { t, duration -> toTestResult(t, duration) },
         )
         val result = testExecutor.execute(test, context)
         results[test] = result
      }
   }
}
