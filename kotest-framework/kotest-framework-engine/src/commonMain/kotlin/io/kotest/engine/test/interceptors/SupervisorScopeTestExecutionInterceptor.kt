package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import kotlinx.coroutines.supervisorScope

/**
 * We don't want any errors in child coroutines to propagate out and cancel all the coroutines used for
 * the specs / parent tests, therefore we install a [supervisorScope]. This scope adds a barrier
 * so that any child coroutines from this point on do not cancel any parent coroutines.
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
