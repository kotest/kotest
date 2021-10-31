package io.kotest.engine.test.interceptors

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.Configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestResult
import io.kotest.engine.test.TestExtensions
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.engine.test.logging.SerialLogExtension
import io.kotest.engine.test.logging.TestScopeLoggingCoroutineContextElement
import io.kotest.engine.test.logging.TestLogger
import kotlinx.coroutines.withContext


@ExperimentalKotest
internal class CoroutineLoggingInterceptor(
   private val configuration: Configuration
) : TestExecutionInterceptor {

   override suspend fun intercept(
      test: suspend (TestCase, TestScope) -> TestResult
   ): suspend (TestCase, TestScope) -> TestResult = { testCase, context ->

      val extensions = TestExtensions(configuration.registry()).logExtensions(testCase)

      when {
         configuration.logLevel.isDisabled() || extensions.isEmpty() -> test(testCase, context)
         else -> {
            val logger = TestLogger(configuration.logLevel)
            try {
               withContext(TestScopeLoggingCoroutineContextElement(logger)) {
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
