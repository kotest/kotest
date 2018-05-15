package io.kotlintest.runner.jvm

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestContext
import io.kotlintest.TestCase

class SharedInstanceSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

  override fun execute(spec: Spec) {
    interceptSpec(spec, {
      // creating the spec instance will have invoked the init block, resulting
      // in the top level test cases being available on the spec class
      topLevelTests(spec).forEach { TestCaseExecutor(listener, it, callingThreadContext(it.description)).execute() }
    })
  }

  private fun callingThreadContext(description: Description): TestContext = object : TestContext() {

    private val seen = HashMap<String, Int>()

    override fun description(): Description = description
    override fun registerTestCase(testCase: TestCase) {
      // if we have a test with this name already, but the line number is different
      // then it's a duplicate test name, so boom
      if (seen.containsKey(testCase.name) && seen[testCase.name] != testCase.line)
        throw IllegalStateException("Cannot add duplicate test name ${testCase.name}")
      seen[testCase.name] = testCase.line
      TestCaseExecutor(listener, testCase, callingThreadContext(testCase.description)).execute()
    }
  }
}