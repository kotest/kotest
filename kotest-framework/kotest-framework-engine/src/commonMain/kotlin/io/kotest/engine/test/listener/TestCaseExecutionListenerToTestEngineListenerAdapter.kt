package io.kotest.engine.test.listener

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.TestCaseExecutionListener

/**
 * Converts events fired to a [TestCaseExecutionListener] into events fired to a [TestEngineListener]
 */
class TestCaseExecutionListenerToTestEngineListenerAdapter(
   private val listener: TestEngineListener
) : TestCaseExecutionListener {

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      listener.testFinished(testCase, result)
   }

   override suspend fun testIgnored(testCase: TestCase, result: TestResult) {
      listener.testIgnored(testCase, result.reasonOrNull)
   }

   override suspend fun testStarted(testCase: TestCase) {
      listener.testStarted(testCase)
   }
}
