package io.kotest.engine.test.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType

/**
 * Checks that the user has not tried to use an invalid invocation count.
 */
object InvocationCountCheckTestExecutionExtension : TestExecutionExtension {
   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult = { context ->
      if (testCase.config.invocations > 1 && testCase.type == TestType.Container)
         error("Cannot execute multiple invocations in parent tests")
      else
         test(context)
   }
}
