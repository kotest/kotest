package io.kotest.engine.test

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

interface TestCaseExecutionListener {
   suspend fun testStarted(testCase: TestCase) {}
   suspend fun testIgnored(testCase: TestCase) {} // I think this breaks the Reason feature, or is Ignored different from Skip?
   suspend fun testFinished(testCase: TestCase, result: TestResult) {}
}

object NoopTestCaseExecutionListener : TestCaseExecutionListener
