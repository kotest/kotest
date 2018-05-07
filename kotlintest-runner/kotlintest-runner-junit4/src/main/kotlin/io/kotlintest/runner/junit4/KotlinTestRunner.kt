package io.kotlintest.runner.junit4

import io.kotlintest.Spec
import io.kotlintest.Scope
import io.kotlintest.TestContainer
import io.kotlintest.runner.jvm.TestRunner
import io.kotlintest.runner.jvm.createSpecInstance
import io.kotlintest.runner.jvm.AsynchronousTestContext
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runners.ParentRunner
import org.junit.runner.notification.RunNotifier

class KotlinTestRunner(private val testClass: Class<out Spec>) : Runner() {

  override fun run(notifier: RunNotifier) {
    val listener = JUnitTestRunnerListener(testClass.kotlin, notifier)
    val runner = TestRunner(listOf(testClass.kotlin), listener)
    runner.execute()
  }

  override fun getDescription(): Description =
      describeScope(createSpecInstance(testClass.kotlin).root())

  /*override fun runChild(child: Scope, notifier: RunNotifier) {
      notifier.fireTestStarted(describeScope(child))
  }

  override fun getChildren(): List<Scope> {
	  val spec = createSpecInstance(testClass.kotlin)
	  val context = AsynchronousTestContext(spec.root())
	  spec.root().closure(context)
	  return context.scopes()
  }

  override fun describeChild(child: Scope): Description =
	  describeScope(child)*/
}
