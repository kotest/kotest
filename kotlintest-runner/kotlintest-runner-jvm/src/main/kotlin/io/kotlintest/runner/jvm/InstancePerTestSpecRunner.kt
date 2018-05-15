package io.kotlintest.runner.jvm

import arrow.core.Failure
import arrow.core.Success
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestContext
import kotlin.reflect.KClass

class InstancePerTestSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

  data class ExecutionContext(val spec: Spec, val target: Description)

  private val executed = HashSet<Description>()

  override fun execute(spec: Spec) {
    topLevelTests(spec).forEach { locateAndExecute(spec::class, it.description) }
  }

  /**
   * When a new [TestCase] is registered, we require a new instance of the [Spec] class.
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
    instantiateSpec(klass).let {
      when (it) {
        is Failure -> throw it.exception
        is Success -> {
          val spec = it.value
          spec.testCases().forEach {
            if (it.description == target) interceptSpec(spec, { io.kotlintest.runner.jvm.TestCaseExecutor(listener, it, targetContext(spec, target)).execute() })
            else if (it.description.isAncestorOf(target)) interceptSpec(spec, { execute(spec, it, target) })
          }
        }
      }
    }
  }

  private fun execute(spec: Spec, current: TestCase, target: Description) {

    val nestedContext = object : TestContext() {
      override fun description(): Description = current.description
      override fun registerTestCase(testCase: TestCase) {
        if (testCase.description == target) {

          if (executed.contains(testCase.description))
            throw IllegalStateException("Cannot add duplicate test name ${testCase.name}")
          executed.add(testCase.description)

          TestCaseExecutor(listener, testCase, targetContext(spec, target)).execute()
        } else if (testCase.description.isAncestorOf(target)) {
          execute(spec, testCase, target)
        }
      }
    }

    current.test.invoke(nestedContext)
  }

  private fun targetContext(spec: Spec, target: Description) = object : TestContext() {
    override fun description(): Description = target
    override fun registerTestCase(testCase: TestCase) = locateAndExecute(spec::class, testCase.description)
  }
}