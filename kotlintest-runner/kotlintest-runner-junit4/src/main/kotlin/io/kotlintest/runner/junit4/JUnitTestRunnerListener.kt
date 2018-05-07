package io.kotlintest.runner.junit4

import io.kotlintest.Spec
import io.kotlintest.TestResult
import io.kotlintest.TestScope
import io.kotlintest.TestStatus
import io.kotlintest.runner.jvm.TestEngineListener
import io.kotlintest.runner.jvm.TestSet
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import kotlin.reflect.KClass
import org.junit.runner.Description as JDescription

class JUnitTestRunnerListener(val testClass: KClass<out Spec>,
                              val notifier: RunNotifier) : TestEngineListener {

  override fun engineStarted(classes: List<KClass<out Spec>>) {}

  override fun engineFinished(t: Throwable?) {}

  override fun prepareSpec(spec: Spec) {}

  override fun completeSpec(spec: Spec, t: Throwable?) {}

  override fun prepareScope(scope: TestScope) {}

  override fun completeScope(scope: TestScope, result: TestResult) {
    val desc = describeScope(scope)
    when (result.status) {
      TestStatus.Success -> notifier.fireTestFinished(desc)
      TestStatus.Error -> notifyFailure(desc, result)
      TestStatus.Ignored -> notifier.fireTestIgnored(desc)
      TestStatus.Failure -> notifyFailure(desc, result)
    }
  }

  override fun prepareTestSet(set: TestSet) {
    notifier.fireTestStarted(desc(set.scope))
  }

  override fun testRun(set: TestSet, k: Int) {}
  override fun completeTestSet(set: TestSet, result: TestResult) {}

  private fun desc(scope: TestScope): JDescription =
      JDescription.createTestDescription(testClass.java.canonicalName, scope.description.tail().fullName())

  private fun notifyFailure(description: JDescription, result: TestResult) {
    notifier.fireTestFailure(Failure(description, result.error))
    notifier.fireTestFinished(description)
  }
}
