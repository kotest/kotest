package io.kotest.engine

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.test.AbstractTestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutionListener

/**
 * A [TestCaseExecutionListener] that completes the Js promise when a test is finished.
 */
internal class PromiseTestCaseExecutionListener(
   private val done: JsTestDoneCallback
) : AbstractTestCaseExecutionListener() {

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      done(result.errorOrNull)
   }
}
