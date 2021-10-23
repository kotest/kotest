package io.kotest.engine.test.interceptors

import io.kotest.core.config.Configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.status.isEnabled
import io.kotest.mpp.log

/**
 * Checks the enabled status of a [TestCase] before invoking it.
 *
 * If the test is disabled, then a [TestResult.Ignored] is returned.
 *
 * Note: This extension must execute before any other extension that invokes methods
 * on the listener, as in runners like junit, ignored cannot happen after "started".
 */
internal class EnabledCheckInterceptor(private val configuration: Configuration) : TestExecutionInterceptor {
   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      val enabled = testCase.isEnabled(configuration)
      when (enabled.isEnabled) {
         true -> {
            log { "EnabledCheckTestExecutionInterceptor: ${testCase.descriptor.path().value} is enabled" }
            test(testCase, context)
         }
         false -> {
            log { "EnabledCheckTestExecutionInterceptor: ${testCase.descriptor.path().value} is disabled" }
            TestResult.Ignored(enabled)
         }
      }
   }
}
