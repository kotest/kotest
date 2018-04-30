package io.kotlintest.runner.junit4

import io.kotlintest.Scope
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.runner.jvm.TestRunnerListener
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import kotlin.reflect.KClass
import org.junit.runner.Description as JDescription

class JUnitTestRunnerListener(val testClass: KClass<out Spec>,
                              val notifier: RunNotifier) : TestRunnerListener {

  private fun desc(scope: Scope): JDescription? =
      when (scope) {
        is TestCase -> JDescription.createTestDescription(testClass.java.canonicalName, scope.description.dropRoot().fullName())
        else -> null
      }

  override fun executionStarted(scope: Scope) {
    notifier.fireTestStarted(describeScope(scope))
  }

  override fun executionFinished(scope: Scope, result: TestResult) {
    val desc = describeScope(scope)
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

  override fun executionStarted() {}

  override fun executionFinished(t: Throwable?) {}
}
