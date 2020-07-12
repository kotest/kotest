package io.kotest.core.engine

import io.kotest.core.runtime.*
import io.kotest.core.spec.Spec
import io.kotest.core.test.*
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.time.ExperimentalTime

/**
 * A [SpecRunner] that executes each leaf [TestCase] in a seperate instance of the containing [Spec].
 *
 * Each test will execute in it's own coroutine, therefore executing concurrently. The user must specify how
 * many threads will be used by the backing dispatcher. Thus, you can specify concurrency with a single
 * threaded pool, or multiple threads, depending on the environment and user requirements.
 */
class InstancePerLeafConcurrentSpecRunner(testEngineListener: TestEngineListener, threads: Int) :
   SpecRunner(testEngineListener) {

   private val dispatcher = Executors.newFixedThreadPool(threads).asCoroutineDispatcher()

   private val results = ConcurrentHashMap<TestCase, TestResult>()

   private val testCaseListener =
      BufferedTestCaseExcecutionListener(TestCaseListenerToTestEngineListenerAdapter(testEngineListener))

   override suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>> {
      return Try {
         coroutineScope {
            spec.rootTests().forEach { rootTest ->
               log("InstancePerLeafConcurrentSpecRunner: Launching coroutine for root test [${rootTest.testCase.description.fullName()}]")
               launch(dispatcher) {
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
      println("Clean spec ${target.description.fullName()}")
      return createInstance(target.spec::class)
         .flatMap { it.invokeBeforeSpec() }
         .flatMap { startTest(it, target.description.names().drop(1)) } // drop the spec name
         .flatMap { it.invokeAfterSpec() }
   }

   // we need to find the same root test but in the newly created spec and begin executing that
   private suspend fun startTest(spec: Spec, targets: List<TestName>): Try<Spec> {
      require(targets.isNotEmpty())
      return Try {
         log("Created new spec instance $spec")
         val root = spec.rootTests().first { it.testCase.description.name == targets.first() }
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
   @OptIn(ExperimentalTime::class)
   private suspend fun run(test: TestCase, targets: List<TestName>) {
      coroutineScope {
         val context = object : TestContext() {

            // the first discovered test should be executed using the same spec
            val open = AtomicBoolean(true)

            // check for duplicate names in the same scope
            val namesInScope = mutableSetOf<TestName>()

            override val testCase: TestCase = test
            override val coroutineContext: CoroutineContext = this@coroutineScope.coroutineContext
            override suspend fun registerTestCase(nested: NestedTest) {

               if (!namesInScope.add(nested.name))
                  throw IllegalStateException("Cannot add duplicate test ${nested.name}")

               val t = nested.toTestCase(test.spec, test.description)

               when {
                  // if the nested test is the next entry that we are looking for, we launch straight into that
                  targets.isNotEmpty() && t.description.name == targets.first() -> launch { run(t, targets.drop(1)) }
                  // if we have no target then we are speculatively executing the scope, so the first nested test
                  // can go on the same spec, any others need to go onto seperate specs.
                  targets.isEmpty() -> {
                     if (open.compareAndSet(true, false)) {
                        launch { run(t, emptyList()) }
                     } else {
                        launch { executeInCleanSpec(t) }
                     }
                  }
               }
            }
         }

         val testExecutor = TestCaseExecutor(testCaseListener, ExecutorExecutionContext)
         val result = testExecutor.execute(test, context)
         results[test] = result
      }
   }
}
