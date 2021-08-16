package io.kotest.engine.test.extensions

import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.withDebugProbe

/**
 * If configured, then the kotlinx debug probe is installed for coroutines.
 * Note: This is a JVM only option.
 */
object CoroutineDebugProbeTestExecutionExtension : TestExecutionExtension {

   override suspend fun shouldApply(testCase: TestCase): Boolean {
      return testCase.config.coroutineDebugProbes ?: configuration.coroutineDebugProbes
   }

   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult = { context ->
      withDebugProbe {
         test(context)
      }
   }
}
