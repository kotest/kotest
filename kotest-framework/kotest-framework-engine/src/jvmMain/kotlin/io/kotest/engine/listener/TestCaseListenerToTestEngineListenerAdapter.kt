package io.kotest.engine.listener

import io.kotest.core.test.TestCaseExecutionListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Converts events fired to a [TestCaseExecutionListener] into events fired to a [TestEngineListener]
 */
class TestCaseListenerToTestEngineListenerAdapter(val listener: TestEngineListener) :
    TestCaseExecutionListener {

   override fun testFinished(testCase: TestCase, result: TestResult) {
      listener.testFinished(testCase, result)
   }

   override fun testIgnored(testCase: TestCase) {
      listener.testIgnored(testCase, null)
   }

   override fun testStarted(testCase: TestCase) {
      listener.testStarted(testCase)
   }
}
