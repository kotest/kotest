package io.kotlintest.specs

import io.kotlintest.ContainerTestDescriptor
import io.kotlintest.Spec
import io.kotlintest.TestCaseDescriptor
import org.junit.platform.engine.TestDescriptor

@Suppress("FunctionName")
abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  fun Given(desc: String, init: GivenScope.() -> Unit) = given(desc, init)
  fun given(desc: String, init: GivenScope.() -> Unit) {
    val descriptor = ContainerTestDescriptor(specDescriptor.uniqueId.append("container", desc), "Given " + desc)
    specDescriptor.addChild(descriptor)
    GivenScope(descriptor).init()
  }

  inner class GivenScope(private val parentDescriptor: TestDescriptor) {
    fun `When`(desc: String, init: WhenScope.() -> Unit) = `when`(desc, init)
    fun `when`(desc: String, init: WhenScope.() -> Unit) {
      val descriptor = ContainerTestDescriptor(specDescriptor.uniqueId.append("container", desc), "When " + desc)
      specDescriptor.addChild(descriptor)
      WhenScope(descriptor).init()
    }
  }

  inner class WhenScope(private val parentDescriptor: TestDescriptor) {
    fun Then(desc: String, test: () -> Unit): TestCaseDescriptor = then(desc, test)
    fun then(desc: String, test: () -> Unit): TestCaseDescriptor {
      val descriptor = TestCaseDescriptor(parentDescriptor.uniqueId.append("test", desc), "Then " + this, source, this@BehaviorSpec, test, defaultTestCaseConfig)
      parentDescriptor.addChild(descriptor)
      return descriptor
    }
  }
}
