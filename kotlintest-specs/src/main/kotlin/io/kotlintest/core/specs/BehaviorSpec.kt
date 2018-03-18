package io.kotlintest.core.specs

import io.kotlintest.core.AbstractSpec
import io.kotlintest.core.TestCase
import io.kotlintest.core.TestCaseDescriptor

@Suppress("FunctionName")
abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun Given(desc: String, init: GivenScope.() -> Unit) = given(desc, init)
  fun given(desc: String, init: GivenScope.() -> Unit) {
    val descriptor = TestCaseDescriptor("Given $desc")
    specDescriptor.addDescriptor(descriptor)
    GivenScope(descriptor).init()
  }

  inner class GivenScope(private val parentDescriptor: TestCaseDescriptor) {
    fun When(desc: String, init: WhenScope.() -> Unit) = `when`(desc, init)
    fun `when`(desc: String, init: WhenScope.() -> Unit) {
      val descriptor = TestCaseDescriptor("When $desc")
      specDescriptor.addDescriptor(descriptor)
      WhenScope(descriptor).init()
    }
  }

  inner class WhenScope(private val parentDescriptor: TestCaseDescriptor) {
    fun Then(desc: String, test: () -> Unit): TestCase = then(desc, test)
    fun then(desc: String, test: () -> Unit): TestCase {
      val tc = TestCase("Then $desc", this@BehaviorSpec, parentDescriptor, test, defaultTestCaseConfig)
      parentDescriptor.addTest(tc)
      return tc
    }
  }
}
