package io.kotest.engine.test.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import kotlinx.coroutines.supervisorScope

object SupervisorScopeTestExecutionExtension : TestExecutionExtension {
   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult {
      // we don't want any errors in the test to propagate out and cancel all the coroutines used for
      // the specs / parent tests, therefore we install a supervisor job
      return { context ->
         supervisorScope { test(context) }
      }
   }
}
