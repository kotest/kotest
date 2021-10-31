package io.kotest.engine.test.interceptors

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestResult
import io.kotest.engine.test.TestExtensions

/**
 * This [TestExecutionInterceptor] executes any user level [TestCaseExtension]s.
 *
 * This extension should happen early, so users can override any disabled status.
 */
internal class TestCaseExtensionInterceptor(private val registry: ExtensionRegistry) : TestExecutionInterceptor {
   override suspend fun intercept(
      test: suspend (TestCase, TestScope) -> TestResult
   ): suspend (TestCase, TestScope) -> TestResult = { testCase, context ->
      TestExtensions(registry).intercept(testCase, context, test)
   }
}
