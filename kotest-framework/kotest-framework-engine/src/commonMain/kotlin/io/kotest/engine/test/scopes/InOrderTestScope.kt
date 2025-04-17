package io.kotest.engine.test.scopes

import io.kotest.core.Logger
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import kotlin.coroutines.CoroutineContext

/**
 * A [TestScope] that executes nested tests as soon as they are discovered.
 */
internal class InOrderTestScope(
   private val specContext: SpecContext,
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext,
   private val mode: DuplicateTestNameMode,
   private val context: EngineContext,
) : TestScope {

   private val logger = Logger(InOrderTestScope::class)
   private var failed = false

   override suspend fun registerTestCase(nested: NestedTest) {
      logger.log { Pair(testCase.name.name, "Nested test case discovered $nested") }
      val nestedTestCase = Materializer(context.specConfigResolver).materialize(nested, testCase)

      val failFast = context.testConfigResolver.failfast(testCase)
      if (failed && failFast) {
         logger.log { Pair(null, "A previous nested test failed and failfast is enabled - will mark this as ignored") }
         val reason = "Failfast enabled on parent test"
         context.listener.testIgnored(nestedTestCase, reason)
         context.testExtensions().ignoredTestListenersInvocation(testCase, reason)
      } else {
         val result = runTest(nestedTestCase, specContext, coroutineContext)
         if (result.isErrorOrFailure) {
            failed = true
         }
      }
   }

   private suspend fun runTest(
      testCase: TestCase,
      specContext: SpecContext,
      coroutineContext: CoroutineContext,
   ): TestResult {
      logger.log { Pair(testCase.name.name, "running test") }
      return TestCaseExecutor(
         TestCaseExecutionListenerToTestEngineListenerAdapter(context.listener),
         context,
      ).execute(
         testCase,
         createSingleInstanceTestScope(
            testCase,
            specContext,
            coroutineContext,
            mode,
            context,
         ),
         specContext,
      )
   }
}
