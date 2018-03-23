package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

@Suppress("FunctionName")
abstract class AbstractBehaviorSpec(body: AbstractBehaviorSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  fun Given(desc: String, init: GivenScope.() -> Unit) = given(desc, init)
  fun given(desc: String, init: GivenScope.() -> Unit) {
    rootScope.addContainer("Given $desc", this@AbstractBehaviorSpec, ::GivenScope, init)
  }

  inner class GivenScope : TestScope() {

    fun and(desc: String, init: GivenScope.() -> Unit) {
      addContainer("And $desc", this@AbstractBehaviorSpec, ::GivenScope, init)
    }

    fun When(desc: String, init: WhenScope.() -> Unit) = `when`(desc, init)
    fun `when`(desc: String, init: WhenScope.() -> Unit) {
      addContainer("When $desc", this@AbstractBehaviorSpec, ::WhenScope, init)
    }
  }

  inner class WhenScope : TestScope() {

    fun and(desc: String, init: WhenScope.() -> Unit) {
      addContainer("And $desc", this@AbstractBehaviorSpec, ::WhenScope, init)
    }

    fun Then(desc: String, test: () -> Unit): TestCase = then(desc, test)
    fun then(desc: String, test: () -> Unit): TestCase =
        addTest("Then $desc", this@AbstractBehaviorSpec, test, defaultTestCaseConfig)
  }
}
