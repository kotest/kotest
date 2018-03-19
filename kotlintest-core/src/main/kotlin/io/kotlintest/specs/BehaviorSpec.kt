package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer

@Suppress("FunctionName")
abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun Given(desc: String, init: GivenScope.() -> Unit) = given(desc, init)
  fun given(desc: String, init: GivenScope.() -> Unit) {
    val descriptor = TestContainer("Given $desc")
    rootContainer.addContainer(descriptor)
    GivenScope(descriptor).init()
  }

  inner class GivenScope(private val parent: TestContainer) {
    fun When(desc: String, init: WhenScope.() -> Unit) = `when`(desc, init)
    fun `when`(desc: String, init: WhenScope.() -> Unit) {
      val descriptor = TestContainer("When $desc")
      rootContainer.addContainer(descriptor)
      WhenScope(descriptor).init()
    }
  }

  inner class WhenScope(private val parent: TestContainer) {
    fun Then(desc: String, test: () -> Unit): TestCase = then(desc, test)
    fun then(desc: String, test: () -> Unit): TestCase {
      val tc = TestCase("Then $desc", nextId(), this@BehaviorSpec, parent, test, defaultTestCaseConfig)
      parent.addTest(tc)
      return tc
    }
  }
}
