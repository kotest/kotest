package io.kotest.engine.test.interceptors

import io.kotest.core.config.Configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.defaultCoroutineDispatcherProvider
import io.kotest.engine.extensions.CoroutineDispatcherAssignerExtension

/**
 * Switches execution onto a dispatcher provided by a [io.kotest.engine.CoroutineDispatcherAssigner].
 */
class CoroutineDispatcherTestExecutionInterceptor(private val configuration: Configuration) : TestExecutionInterceptor {

   override suspend fun intercept(test: suspend (TestCase, TestContext) -> TestResult): suspend (TestCase, TestContext) -> TestResult {

      val ext = configuration.extensions().filterIsInstance<CoroutineDispatcherAssignerExtension>().firstOrNull()
      val provider = ext?.provider() ?: defaultCoroutineDispatcherProvider

      return { testCase, testContext ->
         provider.withDispatcher(testCase) {
            test(testCase, testContext)
         }
      }
   }
}
