package io.kotest.engine.test.extensions

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.Configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.CoroutineDispatcherProvider
import io.kotest.engine.defaultCoroutineDispatcherProvider
import io.kotest.engine.extensions.CoroutineDispatcherExtension
import kotlinx.coroutines.withContext

/**
 * Switches execution onto a dispatcher provided by a [CoroutineDispatcherProvider].
 */
@ExperimentalKotest
class CoroutineDispatcherTestExecutionFilter(private val configuration: Configuration) : TestExecutionFilter {

   override suspend fun execute(test: suspend (TestCase, TestContext) -> TestResult): suspend (TestCase, TestContext) -> TestResult {

      val ext = configuration.extensions().filterIsInstance<CoroutineDispatcherExtension>().firstOrNull()
      val provider = ext?.provider() ?: defaultCoroutineDispatcherProvider

      return { testCase, testContext ->
         val dispatcher = provider.acquire(testCase)
         if (dispatcher == null) {
            test(testCase, testContext)
         } else {
            val result = withContext(dispatcher) {
               test(testCase, testContext)
            }
            provider.release(testCase)
            result
         }
      }
   }
}
