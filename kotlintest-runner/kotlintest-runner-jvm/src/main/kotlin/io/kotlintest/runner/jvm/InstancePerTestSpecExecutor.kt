package io.kotlintest.runner.jvm

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestContext
import io.kotlintest.TestScope
import kotlin.reflect.KClass

class InstancePerTestSpecExecutor(listener: TestEngineListener) : SpecExecutor(listener) {

  data class ExecutionContext(val spec: Spec, val target: Description)

  private val executed = HashSet<Description>()

  override fun execute(spec: Spec) {
    spec.testCases().forEach { locateAndExecute(spec::class, it.description) }
  }

  /**
   * When a new [TestScope] is registered, we require a new instance of the [Spec] class.
   * A new instance is created, and then the root test that matches the required test scope
   * is located. This may be the test itself, or a parent of the test.
   *
   * If it is the test itself then we simply execute it with another context that will
   * call this method.
   *
   * If it's a parent, then we invoke the scope with a context that will keep executing
   * the correct child scope until the actual test is found.
   */
  private fun locateAndExecute(klass: KClass<out Spec>, target: Description) {
    val spec = createSpecInstance(klass)
    spec.testCases().forEach {
      if (it.description == target) interceptSpec(spec, { TestScopeExecutor(listener, it, targetContext(spec, target)).execute() })
      else if (it.description.isAncestorOf(target)) interceptSpec(spec, { execute(spec, it, target) })
    }
  }

  private fun execute(spec: Spec, current: TestScope, target: Description) {

    val nestedContext = object : TestContext() {
      override fun description(): Description = current.description
      override fun registerTestScope(scope: TestScope) {
        if (scope.description == target) {

          if (executed.contains(scope.description))
            throw IllegalStateException("Cannot add duplicate test name ${scope.name}")
          executed.add(scope.description)

          TestScopeExecutor(listener, scope, targetContext(spec, target)).execute()
        } else if (scope.description.isAncestorOf(target)) {
          execute(spec, scope, target)
        }
      }
    }

    current.test.invoke(nestedContext)
  }

  private fun targetContext(spec: Spec, target: Description) = object : TestContext() {
    override fun description(): Description = target
    override fun registerTestScope(scope: TestScope) = locateAndExecute(spec::class, scope.description)
  }
}