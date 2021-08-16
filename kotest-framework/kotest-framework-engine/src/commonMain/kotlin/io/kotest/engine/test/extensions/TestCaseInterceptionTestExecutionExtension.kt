package io.kotest.engine.test.extensions

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.extensions.resolvedExtensions
import io.kotest.engine.test.withCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * This [TestExecutionExtension] executes any user level [TestCaseExtension]s.
 *
 * This extension should happen early, so users can override any disabled status.
 */
object TestCaseInterceptionTestExecutionExtension : TestExecutionExtension {

   /**
    * Returns the runtime [TestCaseExtension]s applicable for this [TestCase].
    * Those are extensions from the test case's [TestCaseConfig] and those applied to
    * the spec instance.
    */
   private fun TestCase.resolvedTestCaseExtensions(): List<TestCaseExtension> {
      return config.extensions + spec.resolvedExtensions().filterIsInstance<TestCaseExtension>()
   }

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
