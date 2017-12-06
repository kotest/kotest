package io.kotlintest.specs

import io.kotlintest.ContainerTestDescriptor
import io.kotlintest.Spec
import io.kotlintest.TestCaseDescriptor
import org.junit.platform.engine.TestDescriptor

abstract class ShouldSpec(body: ShouldSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  operator fun String.invoke(init: ShouldSpecScope.() -> Unit) {
    val descriptor = ContainerTestDescriptor(specDescriptor.uniqueId.append("container", this), this)
    specDescriptor.addChild(descriptor)
    ShouldSpecScope(descriptor).init()
  }

  inner class ShouldSpecScope(private val parentDescriptor: TestDescriptor) {

    operator fun String.invoke(init: ShouldSpecScope.() -> Unit) {
      val descriptor = ContainerTestDescriptor(specDescriptor.uniqueId.append("container", this), this)
      specDescriptor.addChild(descriptor)
      ShouldSpecScope(descriptor).init()
    }

    fun should(name: String, test: () -> Unit): TestCaseDescriptor {
      val descriptor = TestCaseDescriptor(parentDescriptor.uniqueId.append("test", name), "should " + name, source, this@ShouldSpec, test, defaultTestCaseConfig)
      parentDescriptor.addChild(descriptor)
      return descriptor
    }
  }

  fun should(name: String, test: () -> Unit): TestCaseDescriptor {
    val descriptor = TestCaseDescriptor(specDescriptor.uniqueId.append("test", name), "should " + name, source, this@ShouldSpec, test, defaultTestCaseConfig)
    specDescriptor.addChild(descriptor)
    return descriptor
  }
}