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
    val name = "Given $desc"
    rootScopes.add(TestContainer(name, name() + "/" + name, this@AbstractBehaviorSpec, { GivenContext(it).init() }))
  }

  inner class GivenContext(val context: TestContext) {

    fun and(desc: String, init: GivenContext.() -> Unit) {
      val name = "And $desc"
      context.addScope(TestContainer(name, context.currentScope().path() + "/" + name, this@AbstractBehaviorSpec, { GivenContext(it).init() }))
    }

    fun When(desc: String, init: WhenContext.() -> Unit) = `when`(desc, init)
    fun `when`(desc: String, init: WhenContext.() -> Unit) {
      val name = "When $desc"
      context.addScope(TestContainer(name, context.currentScope().path() + "/" + name, this@AbstractBehaviorSpec, { WhenContext(it).init() }))
    }
  }

  inner class WhenContext(val context: TestContext) {

    fun and(desc: String, init: WhenContext.() -> Unit) {
      val name = "And $desc"
      context.addScope(TestContainer(name, context.currentScope().path() + "/" + name, this@AbstractBehaviorSpec, { WhenContext(it).init() }))
    }

    fun Then(desc: String, test: TestContext.() -> Unit): TestCase = then(desc, test)
    fun then(desc: String, test: TestContext.() -> Unit): TestCase {
      val name = "Then $desc"
      val tc = TestCase(name, context.currentScope().path() + "/" + name, this@AbstractBehaviorSpec, test, lineNumber(), defaultTestCaseConfig)
      context.addScope(tc)
      return tc
    }
  }
}
