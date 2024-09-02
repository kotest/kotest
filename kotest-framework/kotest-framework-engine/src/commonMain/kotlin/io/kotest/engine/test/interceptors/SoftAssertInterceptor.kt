package io.kotest.engine.test.interceptors

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.collectiveError
import io.kotest.assertions.errorCollector
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.core.Logger

/**
 * Executes the test with assertSoftly if [assertSoftly] is enabled for this test
 * and if the [TestCase] is a [TestType.Test].
 */
internal class SoftAssertInterceptor() : TestExecutionInterceptor {

   private val logger = Logger(SoftAssertInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      if (testCase.type != TestType.Test) return test(testCase, scope)
      if (!testCase.config.assertSoftly) return test(testCase, scope)

      logger.log { Pair(testCase.name.testName, "Invoking test with soft assert") }
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
