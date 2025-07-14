package io.kotest.engine.test.interceptors

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.getAndReset
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.TestResultBuilder
import kotlin.time.Duration

/**
 * Wraps the test function checking for assertion mode,
 * only if the test is a [TestType.Test].
 */
internal class AssertionModeInterceptor(
   private val testConfigResolver: TestConfigResolver,
) : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      if (testCase.type != TestType.Test) return test(testCase, scope)

      val assertionMode = testConfigResolver.assertionMode(testCase)
      if (assertionMode == AssertionMode.None) return test(testCase, scope)

      val warningMessage = "Test '${testCase.name.name}' did not invoke any assertions"
      assertionCounter.reset()

      val result = test(testCase, scope)
      return when {
         // if we had an error anyway, we don't bother with this check
         result.isErrorOrFailure -> result

         // if we had assertions we're good
         assertionCounter.getAndReset() > 0 -> result

         assertionMode == AssertionMode.Error ->
            TestResultBuilder.builder().withError(ZeroAssertionsError(warningMessage)).build()

         assertionMode == AssertionMode.Warn -> {
            println("Warning: $warningMessage")
            result
         }

         else -> result
      }
   }
}

class ZeroAssertionsError(message: String) : AssertionError(message)
