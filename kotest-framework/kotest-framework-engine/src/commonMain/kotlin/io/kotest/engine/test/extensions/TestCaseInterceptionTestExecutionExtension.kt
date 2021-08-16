package io.kotest.engine.test.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.extensions.resolvedTestCaseExtensions
import io.kotest.engine.test.withCoroutineContext
import kotlin.coroutines.coroutineContext

object TestCaseInterceptionTestExecutionExtension : TestExecutionExtension {

   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult = { context ->

      val innerExecute: suspend (TestContext) -> TestResult = { ctx ->
         test(ctx)
      }

      val execute = testCase.resolvedTestCaseExtensions().foldRight(innerExecute) { extension, execute ->
         { ctx ->
            extension.intercept(testCase) {
               // the user's intercept method is free to change the context of the coroutine
               // to support this, we should switch the context used by the test case context
               execute(ctx.withCoroutineContext(coroutineContext))
            }
         }
      }
      execute(context)
   }
}
