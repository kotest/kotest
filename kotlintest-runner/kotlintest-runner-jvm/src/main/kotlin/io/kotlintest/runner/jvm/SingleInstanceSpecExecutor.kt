package io.kotlintest.runner.jvm

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestContext
import io.kotlintest.TestScope

class SingleInstanceSpecExecutor(listener: TestEngineListener) : SpecExecutor(listener) {

  override fun execute(spec: Spec) {
    interceptSpec(spec, {
      // creating the spec instance will have invoked the init block, resulting
      // in the top level test cases being available on the spec class
      spec.testCases().forEach { TestScopeExecutor(listener, it, callingThreadContext(it.description)).execute() }
    })
  }

  private fun callingThreadContext(description: Description): TestContext = object : TestContext() {
    override fun description(): Description = description
    override fun registerTestScope(scope: TestScope) {
      TestScopeExecutor(listener, scope, callingThreadContext(scope.description)).execute()
    }
  }
}