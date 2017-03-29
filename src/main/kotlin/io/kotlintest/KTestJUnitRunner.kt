package io.kotlintest

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KTestJUnitRunner(testClass: Class<Spec>) : Runner() {

  private val instance = testClass.newInstance()

  override fun getDescription(): Description? = instance.description()

  override fun run(notifier: RunNotifier?): Unit {
    instance.run(notifier!!)
  }
}