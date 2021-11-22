package io.kotest.engine.test.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.Materializer
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.mpp.log
import kotlin.coroutines.CoroutineContext

/**
 * A [TestScope] that executes nested tests as soon as they are discovered.
 */
@ExperimentalKotest
class InOrderTestScope(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext,
   private val mode: DuplicateTestNameMode,
   private val listener: TestEngineListener,
   private val coroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val configuration: ProjectConfiguration,
) : TestScope {

   private var failed = false

   override suspend fun registerTestCase(nested: NestedTest) {
      log { "InOrderTestScope: Nested test case discovered $nested" }
      val nestedTestCase = Materializer(configuration).materialize(nested, testCase)

      if (failed && testCase.config.failfast) {
         log { "InOrderTestScope: A previous nested test failed and failfast is enabled - will mark this as ignored" }
         listener.testIgnored(nestedTestCase, "Failfast enabled on parent test")
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
      return TestCaseExecutor(
         TestCaseExecutionListenerToTestEngineListenerAdapter(listener),
         coroutineDispatcherFactory,
         configuration,
      ).execute(
         testCase,
         createSingleInstanceTestScope(
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
