package io.kotest.engine.test.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.TestTimeoutException
import io.kotest.engine.test.createTestResult
import io.kotest.mpp.log
import io.kotest.mpp.timeInMillis

/**
 * Captures exceptions in downstream test functions and converts to test results.
 * Any [TestExecutionExtension]s that throw should appear after this extension.
 */
class ExceptionCapturingTestExecutionExtension(private val start: Long) : TestExecutionExtension {

   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult = { context ->
      try {
         test(context)
      } catch (e: TestTimeoutException) {
         log { "ExceptionCapturingTestExecutionExtension: TestTimeoutException $e" }
         createTestResult(timeInMillis() - start, e)
      } catch (t: Throwable) {
         log { "ExceptionCapturingTestExecutionExtension: Throwable $t" }
         createTestResult(timeInMillis() - start, t)
      } catch (e: AssertionError) {
         log { "ExceptionCapturingTestExecutionExtension: AssertionError $e" }
         createTestResult(timeInMillis() - start, e)
      }
   }
}
