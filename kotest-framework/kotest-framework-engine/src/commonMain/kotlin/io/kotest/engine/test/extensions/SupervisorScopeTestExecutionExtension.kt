package io.kotest.engine.test.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import kotlinx.coroutines.supervisorScope

/**
 * We don't want any errors in the test to propagate out and cancel all the coroutines used for
 * the specs / a parent tests, therefore we install supervisor job. This supervisor job adds a barrier
 * so that any child coroutines from here do not cancel any parent ones.
 */
object SupervisorScopeTestExecutionExtension : TestExecutionExtension {
   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult {
      return { context ->
         supervisorScope { test(context) }
      }
   }
}
