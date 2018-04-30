package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestContext

@Suppress("FunctionName")
abstract class AbstractBehaviorSpec(body: AbstractBehaviorSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  inner class GivenContext(val context: TestContext) {
    fun And(name: String, test: WhenContext.() -> Unit) = and(name, test)
    fun and(name: String, test: WhenContext.() -> Unit) = add("Add: $name", test)
    fun When(name: String, test: WhenContext.() -> Unit) = `when`(name, test)
    fun `when`(name: String, test: WhenContext.() -> Unit) = add("When: $name", test)
    private fun add(name: String, test: WhenContext.() -> Unit) =
        context.registerTestScope(name, this@AbstractBehaviorSpec, { WhenContext(this).test() }, defaultTestCaseConfig)
  }

  inner class WhenContext(val context: TestContext) {
    fun Then(name: String, test: TestContext.() -> Unit) = then(name, test)
    fun then(name: String, test: TestContext.() -> Unit) =
        context.registerTestScope("Then: $name", this@AbstractBehaviorSpec, test, defaultTestCaseConfig)
  }

  fun Given(name: String, test: GivenContext.() -> Unit) = given(name, test)
  fun given(name: String, test: GivenContext.() -> Unit) =
      addTestCase("Given: $name", { GivenContext(this).test() }, defaultTestCaseConfig)
}
