package io.kotest.engine.spec.execution

import io.kotest.core.Logger
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.DefaultTestScope
import io.kotest.core.test.TestCase
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.TestResults
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.spec.interceptor.SpecInterceptorPipeline
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.names.DuplicateTestNameHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

/**
 * Executor for specs that uses the spec seed instance for all tests.
 */
internal class SingleInstanceSpecExecutor(private val context: EngineContext) : SpecExecutor {

   private val pipeline = SpecInterceptorPipeline(context)
   private val results = TestResults()
   private val materializer = Materializer(context.specConfigResolver)
   private val logger = Logger(SingleInstanceSpecExecutor::class)

   override suspend fun execute(ref: SpecRef, seed: Spec): Result<Map<TestCase, TestResult>> {
      // we switch to a new coroutine for each spec instance, which in this case is always the same provided instance
      return withContext(CoroutineName("spec-scope-" + seed.hashCode())) {
         val specContext = SpecContext.create()
         pipeline.execute(seed, specContext) { spec ->
            launchRootTests(spec, specContext)
            Result.success(results.toMap())
         }.map { results.toMap() } // we only use the test results if the pipeline completes successfully
      }
   }

   private suspend fun launchRootTests(spec: Spec, specContext: SpecContext) {
      logger.log { Pair(spec::class.simpleName, "Launching root tests") }
      val rootTests = materializer.materialize(spec)

      // controls how many tests to execute concurrently
      val concurrency = context.specConfigResolver.testExecutionMode(spec).concurrency
      val semaphore = Semaphore(concurrency)

      // all root test coroutines are launched immediately,
      // the semaphore will control how many can actually run concurrently
      coroutineScope { // will wait for all tests to complete
         rootTests.forEach { root ->
            launch {
               semaphore.withPermit {
                  executeTest(root, specContext)
               }
            }
         }
      }
   }

   /**
    * Executes the given [TestCase] using a [io.kotest.engine.test.TestCaseExecutor].
    * Logs the results in the results tree.
    *
    * @return the result of this single test.
    */
   private suspend fun executeTest(testCase: TestCase, specContext: SpecContext) {

      val duplicateTestNameHandler = DuplicateTestNameHandler()
      val duplicateTestNameMode = context.specConfigResolver.duplicateTestNameMode(testCase.spec)

      val testExecutor = TestCaseExecutor(context)
      val result = testExecutor.execute(
         testCase = testCase,
         testScope = DefaultTestScope(testCase) {
            val unique = duplicateTestNameHandler.unique(duplicateTestNameMode, it.name)
            val uniqueName = it.name.copy(name = unique)
            val nestedTestCase = materializer.materialize(it.copy(name = uniqueName), testCase)
            executeTest(nestedTestCase, specContext)
         },
         specContext = specContext
      )
      results.completed(testCase, result)
   }
}
