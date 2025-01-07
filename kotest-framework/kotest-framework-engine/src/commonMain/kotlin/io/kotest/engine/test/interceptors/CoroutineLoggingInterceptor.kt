package io.kotest.engine.test.interceptors

import io.kotest.common.ExperimentalKotest
import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.test.TestExtensions
import io.kotest.engine.test.logging.SerialLogExtension
import io.kotest.engine.test.logging.TestLogger
import io.kotest.engine.test.logging.TestScopeLoggingCoroutineContextElement
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlinx.coroutines.withContext

@ExperimentalKotest
internal class CoroutineLoggingInterceptor(
   private val projectConfigResolver: ProjectConfigResolver,
   private val testExtensions: TestExtensions,
) : TestExecutionInterceptor {

   private val logger = Logger(CoroutineLoggingInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      val extensions = testExtensions.logExtensions(testCase)
      val logLevel = projectConfigResolver.logLevel()

      return when {
         logLevel.isDisabled() || extensions.isEmpty() -> {
            logger.log { Pair(testCase.name.name, "Test logging is disabled (exts = $extensions)") }
            test(testCase, scope)
         }

         else -> {
            val logger = TestLogger(logLevel)
            withContext(TestScopeLoggingCoroutineContextElement(logger)) {
               test(testCase, scope.withCoroutineContext(coroutineContext))
            }.apply {
               extensions.map { SerialLogExtension(it) }.forEach { extension ->
                  runCatching {
                     extension.handleLogs(testCase, logger.logs.filter { it.level >= logLevel })
                  }
               }
            }
         }
      }
   }
}
