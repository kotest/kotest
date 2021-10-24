package io.kotest.engine.test.contexts

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.toTestCase
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.mpp.log
import kotlin.coroutines.CoroutineContext

/**
 * A [TestContext] that executes nested tests as soon as they are discovered.
 */
@ExperimentalKotest
class InOrderTestContext(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext,
   private val mode: DuplicateTestNameMode,
   private val listener: TestEngineListener,
   private val coroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val configuration: Configuration,
) : TestContext {

   private var failed = false

   override suspend fun registerTestCase(nested: NestedTest) {
      log { "InOrderTestContext: Nested test case discovered $nested" }

      if (failed && testCase.config.failfast == true) {
         log { "FailFastTestContext: A previous nested test failed and failfast is enabled - will mark this as ignored" }
         listener.testIgnored(nested.toTestCase(testCase.spec, testCase), "Failfast enabled on parent test")
      } else {
         val nestedTestCase = nested.toTestCase(testCase.spec, testCase)
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
      return TestCaseExecutor(
         TestCaseExecutionListenerToTestEngineListenerAdapter(listener),
         coroutineDispatcherFactory,
         configuration,
      ).execute(
         testCase,
         createSingleInstanceTestContext(
            testCase,
            coroutineContext,
            mode,
            listener,
            coroutineDispatcherFactory,
            configuration,
         )
      )
   }
}
