package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.CoroutineDispatcherController

/**
 * Switches execution onto a dispatcher provided by the given [CoroutineDispatcherController].
 */
class CoroutineDispatcherInterceptor(private val controller: CoroutineDispatcherController) : TestExecutionInterceptor {

   override suspend fun intercept(test: suspend (TestCase, TestContext) -> TestResult): suspend (TestCase, TestContext) -> TestResult {
      return { testCase, testContext ->
         controller.withDispatcher(testCase) {
            test(testCase, testContext)
         }
      }
   }
}
