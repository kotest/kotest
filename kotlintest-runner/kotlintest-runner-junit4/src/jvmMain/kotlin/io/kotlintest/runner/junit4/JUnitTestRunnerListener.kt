package io.kotlintest.runner.junit4

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.runner.jvm.TestEngineListener
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runner.Description as JDescription

class JUnitTestRunnerListener(private val notifier: RunNotifier) : TestEngineListener {

  override fun enterTestCase(testCase: TestCase) {
    notifier.fireTestStarted(describeTestCase(testCase))
  }

  override fun exitTestCase(testCase: TestCase, result: TestResult) {
    val desc = describeTestCase(testCase)
    when (result.status) {
      TestStatus.Success -> notifier.fireTestFinished(desc)
      TestStatus.Error -> notifyFailure(desc, result)
      TestStatus.Ignored -> notifier.fireTestIgnored(desc)
      TestStatus.Failure -> notifyFailure(desc, result)
    }
  }

  private fun notifyFailure(description: JDescription, result: TestResult) {
    notifier.fireTestFailure(Failure(description, result.error))
    notifier.fireTestFinished(description)
  }
}
