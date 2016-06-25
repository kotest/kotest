package io.kotlintest

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KTestJUnitRunner(val testClass: Class<TestBase>) : Runner() {

  private val instance = testClass.newInstance()

  override fun getDescription(): Description? = instance.getDescription()

  // ok
  override fun run(notifier: RunNotifier?): Unit {
    instance.run(notifier!!)
  }
}