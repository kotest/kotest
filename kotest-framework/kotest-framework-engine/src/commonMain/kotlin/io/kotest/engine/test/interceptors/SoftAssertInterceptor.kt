package io.kotest.engine.test.interceptors

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.collectiveError
import io.kotest.assertions.errorCollector
import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.config.TestConfigResolver

/**
 * Executes the test with assertSoftly if [assertSoftly] is enabled for this test or container.
 */
internal class SoftAssertInterceptor(private val testConfigResolver: TestConfigResolver) : TestExecutionInterceptor {

   private val logger = Logger(SoftAssertInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      if (!testConfigResolver.assertSoftly(testCase)) return test(testCase, scope)

      logger.log { Pair(testCase.name.name, "Invoking test with soft assert") }
      return assertSoftly {
         val result = test(testCase, scope)
         // assertSoftly throws the collected error in finally block,
         // as result test are getting report as execution error instead of assertion error.
         // Here we are collecting error manually and creating failure if error is present so that assertSoftly won't
         // throw any error and test report correctly assertion error instead of execution error.
         errorCollector.collectiveError()?.let { TestResult.Failure(result.duration, it) } ?: result
      }
   }
}
