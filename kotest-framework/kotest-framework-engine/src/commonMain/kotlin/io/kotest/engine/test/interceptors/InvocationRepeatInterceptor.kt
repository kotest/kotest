package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.events.invokeAfterInvocation
import io.kotest.engine.events.invokeBeforeInvocation
import io.kotest.mpp.replay
import kotlin.time.TimeMark

/**
 * A [TestExecutionInterceptor] that repeats a test based on the test's invocations setting.
 */
internal class InvocationRepeatInterceptor(private val timeMark: TimeMark) : TestExecutionInterceptor {

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
      TestResult.Success(timeMark.elapsedNow())
   }
}

