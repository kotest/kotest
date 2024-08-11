package io.kotest.engine.test.interceptors

import io.kotest.assertions.assertionCounterContextElement
import io.kotest.assertions.errorCollectorContextElement
import io.kotest.common.JVMOnly
import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import kotlinx.coroutines.withContext

@JVMOnly
internal actual fun coroutineErrorCollectorInterceptor(): TestExecutionInterceptor =
   CoroutineErrorCollectorInterceptor

/**
 * A [TestExecutionInterceptor] for keeping the error collector synchronized with thread-switching coroutines.
 * Note: This is a JVM only option.
 */
internal object CoroutineErrorCollectorInterceptor : TestExecutionInterceptor {

   private val logger = Logger(CoroutineErrorCollectorInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      logger.log {
         Pair(
            testCase.name.testName,
            "Adding $errorCollectorContextElement and $assertionCounterContextElement to coroutine context"
         )
      }
      return withContext(errorCollectorContextElement + assertionCounterContextElement) {
         test(testCase, scope)
      }
   }
}
