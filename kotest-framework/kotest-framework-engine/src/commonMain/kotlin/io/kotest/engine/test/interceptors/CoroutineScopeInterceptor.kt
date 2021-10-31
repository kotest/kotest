package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestResult
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope

/**
 * Execute the test case wrapped in a [coroutineScope], so that we wait for any child coroutines launched
 * by the user inside the test function to complete before the engine marks the test as completed.
 */
internal object CoroutineScopeInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      test: suspend (TestCase, TestScope) -> TestResult
   ): suspend (TestCase, TestScope) -> TestResult = { testCase, context ->
      log { "CoroutineScopeInterceptor: Creating test coroutine scope" }
      coroutineScope {
         test(
            testCase,
            context.withCoroutineContext(coroutineContext)
         )
      }.apply {
         log { "CoroutineScopeInterceptor: Test execution scope has completed" }
      }
   }
}
