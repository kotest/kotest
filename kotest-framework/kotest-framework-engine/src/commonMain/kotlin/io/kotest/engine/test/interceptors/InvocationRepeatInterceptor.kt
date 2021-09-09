package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.events.invokeAfterInvocation
import io.kotest.engine.events.invokeBeforeInvocation
import io.kotest.mpp.replay
import io.kotest.mpp.timeInMillis

/**
 * A [TestExecutionInterceptor] that repeats a test based on the test's invocations setting.
 */
class InvocationRepeatInterceptor(private val start: Long) : TestExecutionInterceptor {

   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      replay(
         testCase.config.invocations,
         testCase.config.threads,
         { testCase.invokeBeforeInvocation(it) },
         { testCase.invokeAfterInvocation(it) }) {
         test(testCase, context)
      }
      TestResult.success(timeInMillis() - start)
   }
}

