package io.kotlintest.runner.junit4

import arrow.core.Failure
import arrow.core.Success
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.runner.jvm.TestEngine
import io.kotlintest.runner.jvm.instantiateSpec
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KotlinTestRunner(private val testClass: Class<out Spec>) : Runner() {

  override fun run(notifier: RunNotifier) {
    val listener = JUnitTestRunnerListener(notifier)
    val runner = TestEngine(
      listOf(testClass.kotlin),
      emptyList(),
      Project.parallelism(),
      emptySet(),
      emptySet(),
      listener
    )
    runner.execute()
  }

  private val description: Description = testClass.let { klass ->
    instantiateSpec(klass.kotlin).let {
      when (it) {
        is Failure -> throw it.exception
        is Success -> {
          val spec = it.value
          val desc = Description.createSuiteDescription(spec::class.java)
          spec.testCases().forEach { topLevel -> desc.addChild(describeTestCase(topLevel)) }
          desc
        }
      }
    }
  }

  override fun getDescription(): Description = description
}
