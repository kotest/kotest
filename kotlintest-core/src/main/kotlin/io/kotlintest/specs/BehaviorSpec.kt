package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

@Suppress("FunctionName")
abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun Given(desc: String, init: GivenScope.() -> Unit) = given(desc, init)
  fun given(desc: String, init: GivenScope.() -> Unit) {
    val descriptor = TestScope("Given $desc", this@BehaviorSpec)
    rootContainer.addContainer(descriptor)
    GivenScope(descriptor).init()
  }

  inner class GivenScope(private val parent: TestScope) {

    fun and(desc: String, init: GivenScope.() -> Unit) {
      val descriptor = TestScope("And $desc", this@BehaviorSpec)
      parent.addContainer(descriptor)
      GivenScope(descriptor).init()
    }

    fun When(desc: String, init: WhenScope.() -> Unit) = `when`(desc, init)
    fun `when`(desc: String, init: WhenScope.() -> Unit) {
      val descriptor = TestScope("When $desc", this@BehaviorSpec)
      parent.addContainer(descriptor)
      WhenScope(descriptor).init()
    }
  }

  inner class WhenScope(private val parent: TestScope) {

    fun and(desc: String, init: WhenScope.() -> Unit) {
      val descriptor = TestScope("And $desc", this@BehaviorSpec)
      parent.addContainer(descriptor)
      WhenScope(descriptor).init()
    }

    fun Then(desc: String, test: () -> Unit): TestCase = then(desc, test)
    fun then(desc: String, test: () -> Unit): TestCase {
      val tc = TestCase("Then $desc", this@BehaviorSpec, test, defaultTestCaseConfig)
      parent.addTest(tc)
      return tc
    }
  }
}
