package io.kotest.engine.test.interceptors

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.concurrency.withDebugProbe
import io.kotest.mpp.Logger

/**
 * If configured, then the kotlinx debug probe is installed for coroutines.
 * Note: This is a JVM only option.
 */
internal class CoroutineDebugProbeInterceptor(private val configuration: ProjectConfiguration) : TestExecutionInterceptor {

   private val logger = Logger(this::class)

   private fun shouldApply(testCase: TestCase): Boolean {
      return testCase.config.coroutineDebugProbes ?: configuration.coroutineDebugProbes
   }

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      return if (shouldApply(testCase)) {
         logger.log { Pair(testCase.name.testName, "Installing coroutine debug probe") }
         withDebugProbe {
            test(testCase, scope)
         }
      } else {
         test(testCase, scope)
      }
   }
}
