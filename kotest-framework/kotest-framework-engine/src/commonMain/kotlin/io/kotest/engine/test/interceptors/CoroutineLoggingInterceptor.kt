package io.kotest.engine.test.interceptors

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.Configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.TestExtensions
import io.kotest.engine.test.logging.SerialLogExtension
import io.kotest.engine.test.logging.TestLogger
import io.kotest.engine.test.logging.TestScopeLoggingCoroutineContextElement
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlinx.coroutines.withContext

@ExperimentalKotest
internal class CoroutineLoggingInterceptor(private val configuration: Configuration) : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      val extensions = TestExtensions(configuration.registry()).logExtensions(testCase)
      return when {
         configuration.logLevel.isDisabled() || extensions.isEmpty() -> test(testCase, scope)
         else -> {
            val logger = TestLogger(configuration.logLevel)
            try {
               withContext(TestScopeLoggingCoroutineContextElement(logger)) {
                  test(testCase, scope.withCoroutineContext(coroutineContext))
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
