package io.kotest.engine.test.interceptors

import io.kotest.core.config.ExtensionRegistry
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
internal class TestCaseExtensionInterceptor(registry: ExtensionRegistry) : TestExecutionInterceptor {

   private val extensions = TestExtensions(registry)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      return extensions.intercept(testCase, scope) { tc, s -> test(tc, s) }
   }
}
