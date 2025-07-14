package io.kotest.engine.test

import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult

interface TestCaseExecutionListener {
   suspend fun testStarted(testCase: TestCase)
   suspend fun testIgnored(testCase: TestCase, reason: String?)
   suspend fun testFinished(testCase: TestCase, result: TestResult)
}

abstract class AbstractTestCaseExecutionListener : TestCaseExecutionListener {
   override suspend fun testStarted(testCase: TestCase) {}
   override suspend fun testIgnored(testCase: TestCase, reason: String?) {}
   override suspend fun testFinished(testCase: TestCase, result: TestResult) {}
}

object NoopTestCaseExecutionListener : AbstractTestCaseExecutionListener()
