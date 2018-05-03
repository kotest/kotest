package io.kotlintest.runner.jvm

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestContext
import io.kotlintest.TestScope
import kotlin.reflect.KClass

class InstancePerTestSpecExecutor(listener: TestEngineListener) : SpecExecutor(listener) {

  data class ExecutionContext(val spec: Spec, val target: Description)

  override fun execute(spec: Spec) {
    spec.testCases().forEach { locateAndExecute(spec::class, it.description) }
  }

  private fun locateAndExecute(klass: KClass<out Spec>, target: Description) {
    val spec = createSpecInstance(klass)
    spec.testCases().forEach {
      if (it.description == target) interceptSpec(spec, { TestScopeExecutor(listener, it, targetContext(spec, target)).execute() })
      else if (it.description.isAncestorOf(target)) interceptSpec(spec, { execute(spec, it, target) })
    }
  }

  private fun execute(spec: Spec, current: TestScope, target: Description) {
    current.test.invoke(object : TestContext() {
      override fun description(): Description = current.description
      override fun registerTestScope(scope: TestScope) {
        if (scope.description == target) {
          TestScopeExecutor(listener, scope, targetContext(spec, target)).execute()
        } else if (scope.description.isAncestorOf(target)) {
          execute(spec, scope, target)
        }
      }
    })
  }

  private fun targetContext(spec: Spec, target: Description) = object : TestContext() {
    override fun description(): Description = target
    override fun registerTestScope(scope: TestScope) = locateAndExecute(spec::class, scope.description)
  }
}