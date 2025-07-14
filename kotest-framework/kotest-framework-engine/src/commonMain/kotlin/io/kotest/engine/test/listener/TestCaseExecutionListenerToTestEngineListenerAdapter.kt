package io.kotest.engine.test.listener

import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.core.Logger

/**
 * Converts events fired to a [TestCaseExecutionListener] into events fired to a [TestEngineListener]
 */
class TestCaseExecutionListenerToTestEngineListenerAdapter(
   private val listener: TestEngineListener
) : TestCaseExecutionListener {

   private val logger = Logger(TestCaseExecutionListenerToTestEngineListenerAdapter::class)

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.name, "Adapting testFinished to engine event $result $testCase") }
      listener.testFinished(testCase, result)
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      logger.log { Pair(testCase.name.name, "Adapting testIgnored to engine event $reason $testCase") }
      listener.testIgnored(testCase, reason)
   }

   override suspend fun testStarted(testCase: TestCase) {
      logger.log { Pair(testCase.name.name, "Adapting testStarted to engine event $testCase") }
      listener.testStarted(testCase)
   }
}
