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
    notifier.fireTestStarted(desc(scope))
  }

  override fun executionFinished(scope: Scope, result: TestResult) {
    val desc = desc(scope)
    when (result.status) {
      TestStatus.Success -> notifier.fireTestFinished(desc)
      TestStatus.Error -> notifier.fireTestFailure(Failure(desc, result.error))
      TestStatus.Ignored -> notifier.fireTestIgnored(desc)
      TestStatus.Failure -> notifier.fireTestFailure(Failure(desc, result.error))
    }
  }

  override fun executionStarted() {}

  override fun executionFinished(t: Throwable?) {}
}