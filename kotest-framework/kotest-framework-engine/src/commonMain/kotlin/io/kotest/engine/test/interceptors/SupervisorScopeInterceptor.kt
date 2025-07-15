package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlinx.coroutines.supervisorScope

/**
 * We don't want any errors in child coroutines to propagate out and cancel all the coroutines used for
 * the specs / parent tests, therefore we install a [supervisorScope]. This scope adds a barrier
 * so that any child coroutines from this point on do not cancel any parent coroutines.
 */
internal object SupervisorScopeInterceptor : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      // a timeout in a parent test will still cause this to fail
      return supervisorScope {
         test(testCase, scope.withCoroutineContext(coroutineContext))
      }
   }
}
