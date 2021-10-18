package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.createTestResult
import io.kotest.mpp.log
import kotlin.time.TimeMark

/**
 * Captures exceptions in downstream test functions and converts to test results.
 * Any [TestExecutionInterceptor]s that throw should appear after this extension.
 */
internal class ExceptionCapturingInterceptor(private val timeMark: TimeMark) : TestExecutionInterceptor {

   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      try {
         test(testCase, context).apply {
            log { "ExceptionCapturingInterceptor: Test '${testCase.descriptor.path().value}' completed without exception" }
         }
      } catch (t: Throwable) {
         log { "ExceptionCapturingInterceptor: Throwable $t" }
         createTestResult(timeMark.elapsedNow(), t)
      } catch (e: AssertionError) {
         log { "ExceptionCapturingInterceptor: AssertionError $e" }
         createTestResult(timeMark.elapsedNow(), e)
      }
   }
}
