package io.kotest.engine.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.TestAbortedException
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.interceptors.NextTestExecutionInterceptor
import io.kotest.engine.test.interceptors.TestExecutionInterceptor

/**
 * [TestAbortedException] is an exception that can be used to mark a test as ignored (aborted).
 * This interceptor catches [TestAbortedException]s and converts the result to an [TestResult.Ignored].
 */
@JVMOnly
internal object MarkAbortedExceptionsAsSkippedTestInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return test(testCase, scope).let { testResult ->
         val error = testResult.errorOrNull
         when (error) {
            is TestAbortedException -> TestResult.Ignored(error.reason)
            else -> testResult
         }
      }
   }
}

