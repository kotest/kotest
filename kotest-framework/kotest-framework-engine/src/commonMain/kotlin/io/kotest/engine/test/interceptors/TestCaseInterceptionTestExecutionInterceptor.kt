package io.kotest.engine.test.interceptors

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.extensions.resolvedExtensions
import io.kotest.engine.test.withCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * This [TestExecutionInterceptor] executes any user level [TestCaseExtension]s.
 *
 * This extension should happen early, so users can override any disabled status.
 */
object TestCaseInterceptionTestExecutionInterceptor : TestExecutionInterceptor {

   /**
    * Returns the runtime [TestCaseExtension]s applicable for this [TestCase].
    * Those are extensions from the test case's [TestCaseConfig] and those applied to
    * the spec instance.
    */
   private fun TestCase.resolvedTestCaseExtensions(): List<TestCaseExtension> {
      return config.extensions + spec.resolvedExtensions().filterIsInstance<TestCaseExtension>()
   }

   override suspend fun execute(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->

      val execute = testCase.resolvedTestCaseExtensions().foldRight(test) { extension, execute ->
         { tc, ctx ->
            extension.intercept(tc) {
               // the user's intercept method is free to change the context of the coroutine
               // to support this, we should switch the context used by the test case context
               execute(it, ctx.withCoroutineContext(coroutineContext))
            }
         }
      }

      execute(testCase, context)
   }
}
