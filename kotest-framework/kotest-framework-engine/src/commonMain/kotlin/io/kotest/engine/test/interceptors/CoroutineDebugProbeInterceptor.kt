package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.concurrency.withDebugProbe
import io.kotest.core.Logger

/**
 * If configured, then the kotlinx debug probe is installed for coroutines.
 * Note: This is a JVM only option.
 */
internal object CoroutineDebugProbeInterceptor : TestExecutionInterceptor {

   private val logger = Logger(this::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      return if (testCase.config.coroutineDebugProbes) {
         logger.log { Pair(testCase.name.testName, "Installing coroutine debug probe") }
         withDebugProbe {
            test(testCase, scope)
         }
      } else {
         test(testCase, scope)
      }
   }
}
