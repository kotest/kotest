package io.kotest.runner.junit4

import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier

class JUnitTestEngineListener(
   private val notifier: RunNotifier,
) : TestEngineListener {

   override fun testStarted(testCase: TestCase) {
      notifier.fireTestStarted(describeTestCase(testCase))
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      val desc = describeTestCase(testCase)
      when (result.status) {
         TestStatus.Success -> notifier.fireTestFinished(desc)
         TestStatus.Error -> notifyFailure(desc, result)
         TestStatus.Ignored -> notifier.fireTestIgnored(desc)
         TestStatus.Failure -> notifyFailure(desc, result)
      }
   }

   private fun notifyFailure(desc: Description, result: TestResult) {
      notifier.fireTestFailure(Failure(desc, result.error))
      notifier.fireTestFinished(desc)
   }
}
