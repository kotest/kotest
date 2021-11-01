package io.kotest.engine.test.interceptors

import io.kotest.assertions.errorCollectorContextElement
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import kotlinx.coroutines.withContext

internal actual fun coroutineErrorCollectorInterceptor(): TestExecutionInterceptor =
   CoroutineErrorCollectorInterceptor

/**
 * A [TestExecutionInterceptor] for keeping the error collector synchronized with thread-switching coroutines.
 * Note: This is a JVM only option.
 */
internal object CoroutineErrorCollectorInterceptor : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      return withContext(errorCollectorContextElement) {
         test(testCase, scope)
      }
   }
}
