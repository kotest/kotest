package io.kotlintest.specs

import io.kotlintest.ContainerTestDescriptor
import io.kotlintest.Spec
import io.kotlintest.TestCaseDescriptor
import org.junit.platform.engine.TestDescriptor

abstract class FreeSpec(body: FreeSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  infix operator fun String.minus(init: FreeSpecScope.() -> Unit) {
    val descriptor = ContainerTestDescriptor(specDescriptor.uniqueId.append("container", this), this)
    specDescriptor.addChild(descriptor)
    FreeSpecScope(descriptor).init()
  }

  inner class FreeSpecScope(private val parentDescriptor: TestDescriptor) {
    infix operator fun String.invoke(test: () -> Unit): TestCaseDescriptor {
      val descriptor = TestCaseDescriptor(parentDescriptor.uniqueId.append("test", this), this, source, this@FreeSpec, test, defaultTestCaseConfig)
      parentDescriptor.addChild(descriptor)
      return descriptor
    }
  }
}