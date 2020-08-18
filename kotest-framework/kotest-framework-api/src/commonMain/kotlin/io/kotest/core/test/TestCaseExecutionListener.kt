package io.kotest.core.test

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

interface TestCaseExecutionListener {
   fun testStarted(testCase: TestCase) {}
   fun testIgnored(testCase: TestCase) {}
   fun testFinished(testCase: TestCase, result: TestResult) {}
}
