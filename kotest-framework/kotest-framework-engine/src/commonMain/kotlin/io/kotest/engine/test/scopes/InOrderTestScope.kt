package io.kotest.engine.test.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.Materializer
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.mpp.Logger
import kotlin.coroutines.CoroutineContext

/**
 * A [TestScope] that executes nested tests as soon as they are discovered.
 */
internal class InOrderTestScope(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext,
   private val mode: DuplicateTestNameMode,
   private val coroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val context: EngineContext,
) : TestScope {

   private val logger = Logger(InOrderTestScope::class)
   private var failed = false

   override suspend fun registerTestCase(nested: NestedTest) {
      logger.log { Pair(testCase.name.testName, "Nested test case discovered $nested") }
      val nestedTestCase = Materializer(context.configuration).materialize(nested, testCase)

      if (failed && (testCase.config.failfast || context.configuration.projectWideFailFast)) {
         logger.log { Pair(null, "A previous nested test failed and failfast is enabled - will mark this as ignored") }
         context.listener.testIgnored(nestedTestCase, "Failfast enabled on parent test")
      } else {
         val result = runTest(nestedTestCase, coroutineContext)
         if (result.isErrorOrFailure) {
            failed = true
         }
      }
   }

   private suspend fun runTest(
      testCase: TestCase,
      coroutineContext: CoroutineContext,
   ): TestResult {
      logger.log { Pair(testCase.name.testName, "running test") }
      return TestCaseExecutor(
         TestCaseExecutionListenerToTestEngineListenerAdapter(context.listener),
         coroutineDispatcherFactory,
         context,
      ).execute(
         testCase,
         createSingleInstanceTestScope(
            testCase,
            coroutineContext,
            mode,
            coroutineDispatcherFactory,
            context,
         )
      )
   }
}
