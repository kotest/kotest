package io.kotest.engine.test.interceptors

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.Configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.TestExtensions
import io.kotest.engine.test.contexts.withCoroutineContext
import io.kotest.engine.test.logging.SerialLogExtension
import io.kotest.engine.test.logging.TestContextLoggingCoroutineContextElement
import io.kotest.engine.test.logging.TestLogger
import kotlinx.coroutines.withContext


@ExperimentalKotest
internal class CoroutineLoggingInterceptor(
   private val configuration: Configuration
) : TestExecutionInterceptor {

   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->

      val extensions = TestExtensions(configuration.registry()).logExtensions(testCase)

      when {
         configuration.logLevel.isDisabled() || extensions.isEmpty() -> test(testCase, context)
         else -> {
            val logger = TestLogger(configuration.logLevel)
            try {
               withContext(TestContextLoggingCoroutineContextElement(logger)) {
                  test(testCase, context.withCoroutineContext(coroutineContext))
               }
            } catch (ex: Exception) {
               throw ex
            } finally {
               extensions.map { SerialLogExtension(it) }.forEach { extension ->
                  runCatching {
                     extension.handleLogs(testCase, logger.logs.filter { it.level >= configuration.logLevel })
                  }
               }
            }
         }
      }
   }
}
