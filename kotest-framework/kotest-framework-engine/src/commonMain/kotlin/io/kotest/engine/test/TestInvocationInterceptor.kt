package io.kotest.engine.test

import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.concurrency.replay
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.interceptors.NextTestExecutionInterceptor
import io.kotest.engine.test.interceptors.TestExecutionInterceptor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.TimeMark

/**
 * Invokes downstream interceptors one or more times depending on the invocation count in test config.
 */
internal class TestInvocationInterceptor(
   private val timeMark: TimeMark,
   private val invocationInterceptors: List<TestExecutionInterceptor>,
   private val testConfigResolver: TestConfigResolver,
   private val testExtensions: TestExtensions,
) : TestExecutionInterceptor {

   private val logger = Logger(TestInvocationInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor,
   ): TestResult {
      return try {
         invokeWithRetry(testCase, scope, test, 0)
         logger.log { Pair(testCase.name.name, "Test returned without error") }
         TestResult.Success(timeMark.elapsedNow())
      } catch (t: Throwable) {
         logger.log { Pair(testCase.name.name, "Test threw error $t") }
         createTestResult(timeMark.elapsedNow(), t)
      }
   }

   private suspend fun invokeWithRetry(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor,
      attemptedRetries: Int,
   ) {
      try {
         // we wrap in a coroutine scope so that we wait for any user-launched coroutines to finish,
         // and so we can grab any exceptions they throw
         coroutineScope {
            replay(testConfigResolver.invocations(testCase)) {
               runBeforeTestAfter(testCase, scope, it, test)
            }
         }
      } catch (t: Throwable) {
         if (shouldRetry(attemptedRetries, testCase)) {
            delay(retryDelay(testCase))
            invokeWithRetry(testCase, scope, test, attemptedRetries + 1)
         } else throw t
      }
   }

   private fun retryDelay(testCase: TestCase): Duration {
      val retryDelay = testConfigResolver.retryDelay(testCase)
      return when {
         retryDelay != null -> retryDelay
         else -> Duration.ZERO
      }
   }

   private fun shouldRetry(
      attemptedRetries: Int,
      testCase: TestCase,
   ): Boolean {
      val retries = testConfigResolver.retries(testCase)
      return when {
         retries != null -> attemptedRetries < retries
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
            testExtensions.beforeInvocation(tc, times)
            test(tc, sc)
         } finally {
            testExtensions.afterInvocation(tc, times)
         }
      }

      val wrappedTest: NextTestExecutionInterceptor =
         invocationInterceptors.foldRight(executeWithBeforeAfter) { ext, fn ->
            NextTestExecutionInterceptor { tc, tscope -> ext.intercept(tc, tscope, fn) }
         }

      wrappedTest(testCase, scope)
   }
}
