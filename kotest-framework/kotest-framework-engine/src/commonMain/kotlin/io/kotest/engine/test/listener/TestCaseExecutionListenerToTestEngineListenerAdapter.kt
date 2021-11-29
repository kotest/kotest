package io.kotest.engine.test.listener

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.Node
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.TestCaseExecutionListener

/**
 * Converts events fired to a [TestCaseExecutionListener] into events fired to a [TestEngineListener]
 */
class TestCaseExecutionListenerToTestEngineListenerAdapter(
   private val listener: TestEngineListener
) : TestCaseExecutionListener {

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      listener.executionFinished(Node.Test(testCase), result)
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      listener.executionIgnored(Node.Test(testCase), reason)
   }

   override suspend fun testStarted(testCase: TestCase) {
      listener.executionStarted(Node.Test(testCase))
   }
}
