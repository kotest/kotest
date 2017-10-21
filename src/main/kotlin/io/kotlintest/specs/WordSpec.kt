package io.kotlintest.specs

import io.kotlintest.ContainerTestDescriptor
import io.kotlintest.Spec
import io.kotlintest.TestCaseDescriptor
import org.junit.platform.engine.TestDescriptor

abstract class WordSpec(body: WordSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  infix fun String.should(init: ShouldScope.() -> Unit) {
    val descriptor = ContainerTestDescriptor(specDescriptor.uniqueId.append("container", this), this)
    specDescriptor.addChild(descriptor)
    ShouldScope(descriptor).init()
  }

  inner class ShouldScope(private val parentDescriptor: TestDescriptor) {
    infix operator fun String.invoke(test: () -> Unit): TestCaseDescriptor {
      val descriptor = TestCaseDescriptor(parentDescriptor.uniqueId.append("test", this), "should " + this, source, this@WordSpec, test, defaultTestCaseConfig)
      parentDescriptor.addChild(descriptor)
      return descriptor
    }
  }
}

