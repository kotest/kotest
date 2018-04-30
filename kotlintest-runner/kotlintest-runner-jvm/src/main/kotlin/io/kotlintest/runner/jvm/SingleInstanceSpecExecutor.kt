package io.kotlintest.runner.jvm

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestContext
import io.kotlintest.TestScope

class SingleInstanceSpecExecutor(listener: TestEngineListener) : SpecExecutor(listener) {

  override fun execute(spec: Spec) {
    try {
      listener.executionStarted(spec)
      interceptSpec(spec, {
        // creating the spec instance will have invoked the init block, resulting
        // in the top level test cases being available on the spec class
        spec.testCases().forEach { executeTestCase(it, callingThreadContext(it.description)) }
      })
      listener.executionFinished(spec, null)
    } catch (t: Throwable) {
      listener.executionFinished(spec, t)
    }
  }

  private fun callingThreadContext(description: Description): TestContext = object : TestContext() {
    override fun description(): Description = description
    override fun registerTestScope(scope: TestScope) {
      executeTestCase(scope, callingThreadContext(scope.description))
    }
  }
}