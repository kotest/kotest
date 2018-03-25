package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.TestContext
import io.kotlintest.lineNumber

@Suppress("FunctionName")
abstract class AbstractBehaviorSpec(body: AbstractBehaviorSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  fun Given(desc: String, init: GivenContext.() -> Unit) = given(desc, init)
  fun given(desc: String, init: GivenContext.() -> Unit) {
    rootScopes.add(TestContainer("Given $desc", this@AbstractBehaviorSpec, { GivenContext(it).init() }))
  }

  inner class GivenContext(val context: TestContext) {

    fun and(desc: String, init: GivenContext.() -> Unit) {
      context.addScope(TestContainer("And $desc", this@AbstractBehaviorSpec, { GivenContext(it).init() }))
    }

    fun When(desc: String, init: WhenContext.() -> Unit) = `when`(desc, init)
    fun `when`(desc: String, init: WhenContext.() -> Unit) {
      context.addScope(TestContainer("When $desc", this@AbstractBehaviorSpec, { WhenContext(it).init() }))
    }
  }

  inner class WhenContext(val context: TestContext) {

    fun and(desc: String, init: WhenContext.() -> Unit) {
      context.addScope(TestContainer("And $desc", this@AbstractBehaviorSpec, { WhenContext(it).init() }))
    }

    fun Then(desc: String, test: TestContext.() -> Unit): TestCase = then(desc, test)
    fun then(desc: String, test: TestContext.() -> Unit): TestCase {
      val tc = TestCase("Then $desc", this@AbstractBehaviorSpec, test, lineNumber(), defaultTestCaseConfig)
      context.addScope(tc)
      return tc
    }
  }
}
