package io.kotest.engine.test.interceptors

import io.kotest.core.config.Configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.defaultCoroutineDispatcherProvider
import io.kotest.engine.extensions.CoroutineDispatcherExtension
import kotlinx.coroutines.withContext

/**
 * Switches execution onto a dispatcher provided by a [io.kotest.engine.CoroutineDispatcherProvider].
 */
class CoroutineDispatcherTestExecutionInterceptor(private val configuration: Configuration) : TestExecutionInterceptor {

   override suspend fun execute(test: suspend (TestCase, TestContext) -> TestResult): suspend (TestCase, TestContext) -> TestResult {

      val ext = configuration.extensions().filterIsInstance<CoroutineDispatcherExtension>().firstOrNull()
      val provider = ext?.provider() ?: defaultCoroutineDispatcherProvider

      return { testCase, testContext ->
         when (val dispatcher = provider.acquire(testCase)) {
            null -> test(testCase, testContext)
            else -> {
               val result = withContext(dispatcher) {
                  test(testCase, testContext)
               }
               provider.release(testCase)
               result
            }
         }
      }
   }

}
