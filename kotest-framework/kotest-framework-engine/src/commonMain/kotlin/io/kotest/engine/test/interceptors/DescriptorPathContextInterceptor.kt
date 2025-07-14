package io.kotest.engine.test.interceptors

import io.kotest.common.DescriptorPathContextElement
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestScope
import kotlinx.coroutines.withContext

/**
 * Puts the test path into the coroutine context.
 */
internal object DescriptorPathContextInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return withContext(DescriptorPathContextElement(testCase.descriptor.path())) {
         test(testCase, scope)
      }
   }
}

