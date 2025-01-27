package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.concurrency.withDebugProbe
import io.kotest.engine.config.TestConfigResolver

/**
 * If configured, then the kotlinx debug probe is installed for coroutines.
 * Note: This is a JVM only option.
 */
@JVMOnly
internal class CoroutineDebugProbeInterceptor(
   private val testConfigResolver: TestConfigResolver,
) : TestExecutionInterceptor {

   private val logger = Logger(this::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return if (testConfigResolver.coroutineDebugProbes(testCase)) {
         logger.log { Pair(testCase.name.name, "Installing coroutine debug probe") }
         withDebugProbe {
            test(testCase, scope)
         }
      } else {
         test(testCase, scope)
      }
   }
}
