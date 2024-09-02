package io.kotest.engine.test.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope

/**
 * [TestExecutionInterceptor]s are invoked around a [TestCase].
 * They have the ability to skip tests, adjust the test case metadata, and
 * adjust test results.
 */
internal interface TestExecutionInterceptor {
   suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult
}

/**
 * Callback for invoking the next TestExecutionInterceptor.
 *
 * This is a functional interface to reduce the size of stack traces - type-erased lambda types add excess stack lines.
 */
@KotestInternal
fun interface NextTestExecutionInterceptor {
   suspend operator fun invoke(testCase: TestCase, scope: TestScope): TestResult
}
