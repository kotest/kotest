package io.kotest.engine.test.interceptors

import io.kotest.common.TestNameContextElement
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestScope
import kotlinx.coroutines.withContext

/**
 * Puts the test name into the coroutine context.
 */
internal object TestNameContextInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return withContext(TestNameContextElement(testCase.name.name)) {
         test(testCase, scope)
      }
   }
}
