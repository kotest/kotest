package io.kotest.engine.test.registration

import io.kotest.core.config.Configuration
import io.kotest.core.spec.Registration
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.Materializer
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.mpp.Logger

/**
 * A [Registration] that will ignore any nested test cases if another test
 * case has already failed.
 */
class FailFastRegistration(
   private val testCase: TestCase,
   private val listener: TestCaseExecutionListener,
   private val configuration: Configuration,
   private val delegate: Registration,
) : Registration {

   private var failed = false
   private val logger = Logger(this::class)

   override suspend fun runNestedTestCase(nested: NestedTest): TestResult? {
      return if (failed && testCase.config.failfast) {
         val nestedTestCase = Materializer(configuration).materialize(nested, testCase)
         logger.log { Pair(testCase.name.testName, "Failfast enabled - will ignore this nested test") }
         val result = TestResult.Ignored("Failfast enabled on parent test")
         listener.testIgnored(nestedTestCase, result)
         result
      } else {
         val result = delegate.runNestedTestCase(nested)
         if (result != null && result.isErrorOrFailure) {
            failed = true
         }
         result
      }
   }
}
