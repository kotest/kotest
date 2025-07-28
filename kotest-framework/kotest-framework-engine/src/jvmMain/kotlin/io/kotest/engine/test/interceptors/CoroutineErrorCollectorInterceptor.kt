package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.test.TestResult
import io.kotest.matchers.assertionCounterContextElement
import io.kotest.matchers.errorCollectorContextElement
import kotlinx.coroutines.withContext

@JVMOnly
internal actual fun coroutineErrorCollectorInterceptor(): TestExecutionInterceptor =
   CoroutineErrorCollectorInterceptor

/**
 * A [TestExecutionInterceptor] for keeping the error collector and assertion counter
 * synchronized with thread-switching coroutines.
 *
 * Note: This is a JVM only option.
 */
@JVMOnly
internal object CoroutineErrorCollectorInterceptor : TestExecutionInterceptor {

   private val logger = Logger(CoroutineErrorCollectorInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      logger.log {
         Pair(
            testCase.name.name,
            "Adding $errorCollectorContextElement and $assertionCounterContextElement to coroutine context"
         )
      }
      return withContext(errorCollectorContextElement + assertionCounterContextElement) {
         test(testCase, scope)
      }
   }
}
