package io.kotest.engine.test.interceptors

import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.status.isEnabled
import kotlin.time.Duration

/**
 * Checks the enabled status of a [TestCase] before invoking it.
 *
 * If the test is disabled, then a [TestResult.Ignored] is returned.
 *
 * Note: This extension must execute before any other extension that invokes lifecycle callbacks
 * on the test engine listener, because in reporters like TeamCity or junit, ignored cannot happen after started.
 */
internal class TestEnabledCheckInterceptor(
   private val projectConfigResolver: ProjectConfigResolver,
   private val specConfigResolver: SpecConfigResolver,
   private val testConfigResolver: TestConfigResolver,
) : TestExecutionInterceptor {

   private val logger = Logger(TestEnabledCheckInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return runCatching { testCase.isEnabled(projectConfigResolver, specConfigResolver, testConfigResolver) }.fold(
         { enabled ->
            when (enabled.isEnabled) {
               true -> {
                  logger.log { Pair(testCase.name.name, "Test is enabled") }
                  test(testCase, scope)
               }

               false -> {
                  logger.log { Pair(testCase.name.name, "Test is disabled: ${enabled.reason}") }
                  TestResult.Ignored(enabled)
               }
            }
         },
         { t ->
            logger.log { Pair(testCase.name.name, "Error running enabled checks") }
            TestResult.Error(Duration.ZERO, t)
         }
      )
   }
}
