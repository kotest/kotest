package io.kotest.engine.test.interceptors

import io.kotest.core.spec.Registration
import io.kotest.core.spec.RegistrationContextElement
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import kotlinx.coroutines.withContext

/**
 * Adds the given [registration] to the coroutine scope used for this test execution.
 */
class RegistrationInterceptor(private val registration: Registration) : TestExecutionInterceptor {
   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      return withContext(RegistrationContextElement(registration)) {
         test(testCase, scope)
      }
   }
}
