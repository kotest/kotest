package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.createTestResult
import io.kotest.mpp.log
import io.kotest.mpp.timeInMillis

/**
 * Captures exceptions in downstream test functions and converts to test results.
 * Any [TestExecutionInterceptor]s that throw should appear after this extension.
 */
internal class ExceptionCapturingInterceptor(private val start: Long) : TestExecutionInterceptor {

   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      try {
         test(testCase, context).apply {
            log { "ExceptionCapturingInterceptor: Test completed without exception" }
         }
      } catch (t: Throwable) {
         log { "ExceptionCapturingInterceptor: Throwable $t" }
         createTestResult(timeInMillis() - start, t)
      } catch (e: AssertionError) {
         log { "ExceptionCapturingInterceptor: AssertionError $e" }
         createTestResult(timeInMillis() - start, e)
      }
   }
}
