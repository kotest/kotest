package io.kotest.core.test

interface TestCaseExecutionListener {
   fun testStarted(testCase: TestCase) {}
   fun testIgnored(testCase: TestCase) {}
   fun testFinished(testCase: TestCase, result: TestResult) {}
}
