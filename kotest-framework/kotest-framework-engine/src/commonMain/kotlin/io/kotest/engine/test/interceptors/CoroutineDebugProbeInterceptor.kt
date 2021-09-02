package io.kotest.engine.test.interceptors

import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.withDebugProbe

/**
 * If configured, then the kotlinx debug probe is installed for coroutines.
 * Note: This is a JVM only option.
 */
object CoroutineDebugProbeInterceptor : TestExecutionInterceptor {

   private fun shouldApply(testCase: TestCase): Boolean {
      return testCase.config.coroutineDebugProbes ?: configuration.coroutineDebugProbes
   }

   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      if (shouldApply(testCase)) {
         withDebugProbe {
            test(testCase, context)
         }
      } else {
         test(testCase, context)
      }
   }
}
