package io.kotlintest.runner.jvm

import arrow.core.Failure
import arrow.core.Success
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestContext
import kotlin.reflect.KClass

class InstancePerTestSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

  private val executed = HashSet<Description>()

  override fun execute(spec: Spec) {
    // we start by executing each top level test in the spec, which in turn will lead
    // to further test discoveries
    topLevelTests(spec).forEach { locateAndExecute(spec::class, it.description) }
  }

  /**
   * When a new [TestCase] is registered, we require a new instance of the [Spec] class.
   *
   * A new instance is created, and then the root test that matches the required test scope
   * is located. This may be the test itself, or a parent of the test.
   *
   * If it is the test itself then we simply execute it with another context that will
   * call this method.
   *
   * If it's a parent, then we invoke the scope with a context that will keep executing
   * the correct child scope until the actual test is found.
   *
   * @param target the test we are looking to execute
   */
  private fun locateAndExecute(klass: KClass<out Spec>, target: Description) {
    instantiateSpec(klass).let {
      when (it) {
        is Failure -> throw it.exception
        is Success -> {
          val spec = it.value
          // for each of the top level tests we check if it is the one we are
          // looking to execute. If it is then we execute it immediately using a context
          // which will invoke any nested tests in turn.
          // If the test is not the one we are looking for, but is infact an ancestor
          // then we need to execute the closure to ensure state is correct. We do this
          // with a context that will perform the same logic as this code, but on the
          // nested test itself.
          spec.testCases().forEach {

            if (it.description == target) interceptSpec(spec) {
              TestCaseExecutor(listener, it, targetContext(spec, target)).execute()
            } else if (it.description.isAncestorOf(target)) {
              interceptSpec(spec) {
                execute(spec, it, target)
              }
            }
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