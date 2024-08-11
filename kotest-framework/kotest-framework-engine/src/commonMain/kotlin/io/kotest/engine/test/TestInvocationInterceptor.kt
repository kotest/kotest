package io.kotest.engine.test

import io.kotest.core.Logger
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.concurrency.replay
import io.kotest.engine.test.interceptors.NextTestExecutionInterceptor
import io.kotest.engine.test.interceptors.TestExecutionInterceptor
import io.kotest.engine.concurrency.replay
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.TimeMark

internal class TestInvocationInterceptor(
   registry: ExtensionRegistry,
   private val timeMark: TimeMark,
   private val invocationInterceptors: List<TestExecutionInterceptor>,
) : TestExecutionInterceptor {

   private val extensions = TestExtensions(registry)
   private val logger = Logger(TestInvocationInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return try {
         invokeWithRetry(testCase, scope, test, 0)
         logger.log { Pair(testCase.name.testName, "Test returned without error") }
         TestResult.Success(timeMark.elapsedNow())
      } catch (t: Throwable) {
         logger.log { Pair(testCase.name.testName, "Test threw error $t") }
         createTestResult(timeMark.elapsedNow(), t)
      }
   }

   private suspend fun invokeWithRetry(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult,
      attemptedRetries: Int,
   ) {
      try {
         // we wrap in a coroutine scope so that we wait for any user-launched coroutines to finish,
         // and so we can grab any exceptions they throw
         coroutineScope {
            replay(
               testCase.config.invocations,
               testCase.config.threads
            )
            { runBeforeTestAfter(testCase, scope, it, test) }
         }
      } catch (t: Throwable) {
         if (shouldRetry(attemptedRetries, testCase)) {
            delay(retryDelay(testCase, attemptedRetries))
            invokeWithRetry(testCase, scope, test, attemptedRetries + 1)
         } else throw t
      }
   }

   private fun retryDelay(testCase: TestCase, attemptedRetries: Int): Duration {
      val retryDelay = testCase.config.retryDelay
      val retryDelayFn = testCase.config.retryDelayFn
      return when {
         retryDelay != null -> retryDelay
         retryDelayFn != null -> retryDelayFn(testCase, attemptedRetries)
         else -> Duration.ZERO
      }
   }

   private fun shouldRetry(
      attemptedRetries: Int,
      testCase: TestCase,
   ): Boolean {
      val retries = testCase.config.retries
      val retryFn = testCase.config.retryFn
      return when {
         retries != null -> attemptedRetries < retries
         retryFn != null -> attemptedRetries < retryFn(testCase)
         else -> false
      }
   }

   private suspend fun runBeforeTestAfter(
      testCase: TestCase,
      scope: TestScope,
      times: Int,
      test: NextTestExecutionInterceptor,
   ) {
      val executeWithBeforeAfter = NextTestExecutionInterceptor { tc, sc ->
         try {
            extensions.beforeInvocation(tc, times)
            test(tc, sc)
         } finally {
            extensions.afterInvocation(tc, times)
         }
      }

      val wrappedTest: NextTestExecutionInterceptor =
         invocationInterceptors.foldRight(executeWithBeforeAfter) { ext, fn ->
            NextTestExecutionInterceptor { tc, tscope -> ext.intercept(tc, tscope, fn) }
         }

      wrappedTest(testCase, scope)
   }
}
