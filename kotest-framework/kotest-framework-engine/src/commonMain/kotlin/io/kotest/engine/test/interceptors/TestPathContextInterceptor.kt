package io.kotest.engine.test.interceptors

import io.kotest.common.TestPathContextElement
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import kotlinx.coroutines.withContext

/**
 * Puts the test path into the coroutine context.
 */
internal object TestPathContextInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return withContext(TestPathContextElement(testCase.descriptor.path(true))) {
         test(testCase, scope)
      }
   }
}

