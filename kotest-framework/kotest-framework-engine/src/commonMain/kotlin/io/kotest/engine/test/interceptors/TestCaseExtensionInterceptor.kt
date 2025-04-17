package io.kotest.engine.test.interceptors

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.TestExtensions

/**
 * This [TestExecutionInterceptor] executes any user level [TestCaseExtension]s.
 *
 * This extension should happen early, so users can override any disabled status.
 */
internal class TestCaseExtensionInterceptor(private val testExtensions: TestExtensions) : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return testExtensions.intercept(testCase, scope) { tc, s -> test(tc, s) }
   }
}
