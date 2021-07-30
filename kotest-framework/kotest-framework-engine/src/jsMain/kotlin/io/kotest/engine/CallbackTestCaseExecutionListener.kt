package io.kotest.engine

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.test.TestCaseExecutionListener

/**
 * A [TestCaseExecutionListener] that will invoke the given [callback] once the test has completed.
 */
class CallbackTestCaseExecutionListener(private val callback: (TestResult) -> Unit) : TestCaseExecutionListener {
   override fun testStarted(testCase: TestCase) {}
   override fun testIgnored(testCase: TestCase) {}
   override fun testFinished(testCase: TestCase, result: TestResult) {
      callback(result)
   }
}
