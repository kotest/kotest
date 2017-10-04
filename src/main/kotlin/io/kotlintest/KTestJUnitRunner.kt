package io.kotlintest

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KTestJUnitRunner(testClass: Class<Spec>) : Runner() {

  init {
    Project.incrementTestSuiteCount()
  }

  private val instance = testClass.newInstance()

  override fun getDescription(): Description? = instance.description()

  override fun run(notifier: RunNotifier?) {
    instance.run(notifier!!)
  }
}