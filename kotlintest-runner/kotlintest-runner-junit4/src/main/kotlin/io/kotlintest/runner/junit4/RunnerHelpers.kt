package io.kotlintest.runner.junit4

import io.kotlintest.*
import io.kotlintest.runner.jvm.TestRunner
import io.kotlintest.runner.jvm.createSpecInstance
import io.kotlintest.runner.jvm.AsynchronousTestContext
import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier

internal tailrec fun describeScope(scope: Scope): Description =
  when(scope) {
      is TestContainer -> describeTestContainer(scope)
      is TestCase -> describeTestCase(scope)
      else -> Description.createSuiteDescription(scope::class.java)
  }


private fun describeTestContainer(container: TestContainer): Description {
  val description = Description
      .createTestDescription(container.sourceClass.java, container.description.fullName())
  
  val context = AsynchronousTestContext(container)
  container.closure(context)
  context.scopes().forEach { description.addChild(describeScope(it)) }
  return description
}

private fun describeTestCase(case: TestCase): Description =
    Description.createTestDescription(case.spec::class.java, case.description.fullName())
