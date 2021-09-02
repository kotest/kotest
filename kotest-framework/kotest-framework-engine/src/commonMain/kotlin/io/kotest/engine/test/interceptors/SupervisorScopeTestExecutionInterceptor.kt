package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import kotlinx.coroutines.supervisorScope

/**
 * We don't want any errors in the test to propagate out and cancel all the coroutines used for
 * the specs / a parent tests, therefore we install supervisor job. This supervisor job adds a barrier
 * so that any child coroutines from here do not cancel any parent ones.
 */
internal object SupervisorScopeTestExecutionInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult {
      return { testCase, context ->
         supervisorScope { test(testCase, context) }
      }
   }
}
