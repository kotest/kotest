package io.kotlintest.runner.junit4

import io.kotlintest.Spec
import io.kotlintest.runner.jvm.TestRunner
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KotlinTestRunner(val testClass: Class<out Spec>) : Runner() {

  override fun run(notifier: RunNotifier) {
    val listener = JUnitTestRunnerListener(testClass.kotlin, notifier)
    val runner = TestRunner(listOf(testClass.kotlin), listener)
    runner.execute()
  }

  override fun getDescription(): Description =
      Description.createSuiteDescription(testClass)

}