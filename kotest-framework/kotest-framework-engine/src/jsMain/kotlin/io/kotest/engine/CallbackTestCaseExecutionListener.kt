package io.kotest.engine

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.test.TestCaseExecutionListener

/**
 * A [TestCaseExecutionListener] that will invoke the given [callback] once the test has completed.
 */
class CallbackTestCaseExecutionListener(private val callback: suspend (TestResult) -> Unit) :
   TestCaseExecutionListener {
   override suspend fun testStarted(testCase: TestCase) {}
   override suspend fun testIgnored(testCase: TestCase) {}
   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      callback(result)
   }
}
