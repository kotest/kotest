package io.kotest.engine.test.interceptors

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.status.isEnabled
import io.kotest.core.Logger

/**
 * Checks the enabled status of a [TestCase] before invoking it.
 *
 * If the test is disabled, then a [TestResult.Ignored] is returned.
 *
 * Note: This extension must execute before any other extension that invokes methods
 * on the listener, as in runners like junit, ignored cannot happen after "started".
 */
internal class TestEnabledCheckInterceptor(private val configuration: ProjectConfiguration) : TestExecutionInterceptor {

   private val logger = Logger(TestEnabledCheckInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      val enabled = testCase.isEnabled(configuration)
      return when (enabled.isEnabled) {
         true -> {
            logger.log { Pair(testCase.name.testName, "Test is enabled") }
            test(testCase, scope)
         }
         false -> {
            logger.log { Pair(testCase.name.testName, "Test is disabled: ${enabled.reason}") }
            TestResult.Ignored(enabled)
         }
      }
   }
}
