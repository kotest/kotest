package io.kotest.engine.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.interceptors.TestExecutionInterceptor
import org.opentest4j.TestAbortedException

/**
 * Writes failed specs to a file so that the [io.kotest.engine.spec.FailureFirstSorter]
 * can use the file to run failed specs first.
 *
 * Note: This is a JVM only feature.
 */
@JVMOnly
internal object MarkAbortedExceptionsAsSkippedTestInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      return test(testCase, scope).let { testResult ->
         if (testResult.errorOrNull is TestAbortedException) {
            TestResult.Ignored()
         } else {
            testResult
         }
      }
   }
}
