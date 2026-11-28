package io.kotest.engine.test

import io.kotest.core.test.TestCase

interface TestCaseExecutionListener {
   suspend fun testStarted(testCase: TestCase)
   suspend fun testIgnored(testCase: TestCase, reason: String?)
   suspend fun testFinished(testCase: TestCase, result: TestResult)

   /**
    * Invoked with the output to report for a test, before [testFinished] for that test.
    */
   suspend fun testOutputCaptured(testCase: TestCase, output: CapturedOutput) {}
}

abstract class AbstractTestCaseExecutionListener : TestCaseExecutionListener {
   override suspend fun testStarted(testCase: TestCase) {}
   override suspend fun testIgnored(testCase: TestCase, reason: String?) {}
   override suspend fun testFinished(testCase: TestCase, result: TestResult) {}
}

object NoopTestCaseExecutionListener : AbstractTestCaseExecutionListener()
