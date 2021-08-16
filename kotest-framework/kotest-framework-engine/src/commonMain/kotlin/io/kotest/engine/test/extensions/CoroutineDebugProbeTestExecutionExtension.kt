package io.kotest.engine.test.extensions

import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.withDebugProbe
import io.kotest.mpp.log

object CoroutineDebugProbeTestExecutionExtension : TestExecutionExtension {
   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult = { context ->

      val enabled = testCase.config.coroutineDebugProbes ?: configuration.coroutineDebugProbes
      log { "TestCaseExecutor: coroutineDebugProbes enabled=$enabled" }

      if (enabled) {
         withDebugProbe {
            test(context)
         }
      } else {
         test(context)
      }
   }
}
