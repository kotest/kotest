package io.kotest.engine.test.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
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
 * A functional interface for the interceptor callback, to reduce the size of stack traces.
 *
 * With a normal lambda type, each call adds three lines to the stacktrace, but an interface only adds one line.
 */
@KotestInternal
fun interface NextTestExecutionInterceptor {
   suspend operator fun invoke(testCase: TestCase, scope: TestScope): TestResult
}
