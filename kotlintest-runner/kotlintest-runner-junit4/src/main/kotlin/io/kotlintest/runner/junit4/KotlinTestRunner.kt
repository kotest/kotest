package io.kotlintest.runner.junit4

import io.kotlintest.Spec
import io.kotlintest.runner.jvm.TestEngine
import io.kotlintest.runner.jvm.createSpecInstance
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KotlinTestRunner(private val testClass: Class<out Spec>) : Runner() {

  override fun run(notifier: RunNotifier) {
    val listener = JUnitTestRunnerListener(testClass.kotlin, notifier)
    val runner = TestEngine(listOf(testClass.kotlin), listener)
    runner.execute()
  }

  private val description: Description = testClass.let {
    val spec = createSpecInstance(it.kotlin)
    val desc = Description.createSuiteDescription(spec::class.java)
    spec.testCases().forEach { desc.addChild(describeTestCase(it)) }
    desc
  }

  override fun getDescription(): Description = description
}
