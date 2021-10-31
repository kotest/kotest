package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestResult
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
actual class TestCoroutineDispatcherInterceptor : TestExecutionInterceptor {

   override suspend fun intercept(
      test: suspend (TestCase, TestScope) -> TestResult
   ): suspend (TestCase, TestScope) -> TestResult = { testCase, context ->
      val dispatcher = TestCoroutineDispatcher()
      test(testCase, context.withCoroutineContext(dispatcher))
   }
}
