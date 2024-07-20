package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.createTestResult
import io.kotest.mpp.Logger
import kotlin.time.TimeMark

/**
 * Captures exceptions from downstream interceptors and converts to a failed test result.
 * Any [TestExecutionInterceptor]s that can throw (e.g. by using [withContext]) should
 * appear after this interceptor.
 */
internal class ExceptionCapturingInterceptor(private val timeMark: TimeMark) : TestExecutionInterceptor {

   private val logger = Logger(ExceptionCapturingInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      return try {
         test(testCase, scope)
      } catch (t: Throwable) {
         logger.log { Pair(testCase.name.testName, "Throwable $t") }
         createTestResult(timeMark.elapsedNow(), t)
      } catch (e: AssertionError) {
         logger.log { Pair(testCase.name.testName, "AssertionError $e") }
         createTestResult(timeMark.elapsedNow(), e)
      }
   }
}
