package io.kotest.engine.test.interceptors

import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestResultBuilder
import io.kotest.engine.test.enabled.TestEnabledChecker

/**
 * Checks the enabled status of a [TestCase] using a [TestEnabledChecker] before invoking it.
 *
 * If the test is disabled, then a [TestResult.Ignored] is returned.
 *
 * Note: This extension must execute before any other extension that invokes lifecycle callbacks
 * on the test engine listener, because in reporters like TeamCity or JUnit, setting an ignored status
 * cannot happen after a started status has been set.
 */
internal class TestEnabledCheckInterceptor(
   projectConfigResolver: ProjectConfigResolver,
   specConfigResolver: SpecConfigResolver,
   testConfigResolver: TestConfigResolver,
) : TestExecutionInterceptor {

   private val logger = Logger(TestEnabledCheckInterceptor::class)
   private val checker = TestEnabledChecker(projectConfigResolver, specConfigResolver, testConfigResolver)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return runCatching { checker.isEnabled(testCase) }.fold(
         { enabled ->
            when (enabled.isEnabled) {
               true -> {
                  logger.log { Pair(testCase.name.name, "Test is enabled") }
                  test(testCase, scope)
               }

               false -> {
                  logger.log { Pair(testCase.name.name, "Test is disabled: ${enabled.reason}") }
                  TestResultBuilder.builder().withIgnoreEnabled(enabled).build()
               }
            }
         },
         { t ->
            logger.log { Pair(testCase.name.name, "Error running enabled checks") }
            TestResultBuilder.builder().withError(t).build()
         }
      )
   }
}
