package io.kotest.engine.spec.runners

import io.kotest.common.ExperimentalKotest
import io.kotest.common.flatMap
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.SpecRunner
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.engine.test.scheduler.TestScheduler
import io.kotest.engine.test.scopes.DuplicateNameHandlingTestScope
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [SpecRunner] that executes all tests against the
 * same [Spec] instance. In other words, only a single instance of the spec class
 * is instantiated for all the test cases.
 */
@ExperimentalKotest
internal class SingleInstanceSpecRunner(
   listener: TestEngineListener,
   scheduler: TestScheduler,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val configuration: ProjectConfiguration,
) : SpecRunner(listener, scheduler, configuration) {

   private val results = ConcurrentHashMap<TestCase, TestResult>()
   private val extensions = SpecExtensions(configuration.registry)
   private val logger = Logger(SingleInstanceSpecRunner::class)

   override suspend fun execute(spec: Spec): Result<Map<TestCase, TestResult>> {
      logger.log { Pair(spec::class.bestName(), "executing spec $spec") }

      suspend fun interceptAndRun(context: CoroutineContext) = runCatching {
         val rootTests = materializer.materialize(spec)
         logger.log { Pair(spec::class.bestName(), "Materialized root tests: ${rootTests.size}") }
         launch(spec) {
            logger.log { Pair(it.name.testName, "Executing test $it") }
            runTest(it, context, null)
         }
      }

      try {
         return coroutineScope {
            extensions.beforeSpec(spec)
               .flatMap { interceptAndRun(coroutineContext) }
               .flatMap { SpecExtensions(configuration.registry).afterSpec(spec) }
               .map { results }
         }
      } catch (e: Exception) {
         e.printStackTrace()
         throw e
      }
   }

   /**
    * A [TestScope] that runs discovered tests as soon as they are registered in the same spec instance.
    *
    * This implementation tracks fail fast if configured via TestCase config or globally.
    */
   inner class SingleInstanceTestScope(
      override val testCase: TestCase,
      override val coroutineContext: CoroutineContext,
      private val parentScope: SingleInstanceTestScope?,
   ) : TestScope {

      // set to true if we failed fast and should ignore further tests
      private var skipRemaining = false

      // in the single instance runner we execute each nested test as soon as they are registered
      override suspend fun registerTestCase(nested: NestedTest) {
         logger.log { Pair(testCase.name.testName, "Registering nested test '${nested}") }

         val nestedTestCase = Materializer(configuration).materialize(nested, testCase)
         if (skipRemaining) {
            logger.log { Pair(testCase.name.testName, "Skipping test due to fail fast") }
            listener.testIgnored(nestedTestCase, "Skipping test due to fail fast")
         } else {
            // if running this nested test results in an error, we won't launch anymore nested tests
            val result = runTest(nestedTestCase, coroutineContext, this@SingleInstanceTestScope)
            if (result.isErrorOrFailure) {
               if (testCase.config.failfast || configuration.projectWideFailFast) {
                  logger.log { Pair(testCase.name.testName, "Test failed - setting skipRemaining = true") }
                  skipRemaining = true
                  parentScope?.skipRemaining = true
               }
            }
         }
      }
   }

   private suspend fun runTest(
      testCase: TestCase,
      coroutineContext: CoroutineContext,
      parentScope: SingleInstanceTestScope?,
   ): TestResult {

      val testExecutor = TestCaseExecutor(
         TestCaseExecutionListenerToTestEngineListenerAdapter(listener),
         defaultCoroutineDispatcherFactory,
         configuration,
      )

      val scope = DuplicateNameHandlingTestScope(
         configuration.duplicateTestNameMode,
         SingleInstanceTestScope(testCase, coroutineContext, parentScope)
      )

      val result = testExecutor.execute(testCase, scope)
      results[testCase] = result
      return result
   }
}
