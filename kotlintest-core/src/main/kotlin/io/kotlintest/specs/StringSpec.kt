package io.kotlintest.specs

import io.kotlintest.Spec
import io.kotlintest.TestCaseDescriptor

abstract class StringSpec(body: StringSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  operator fun String.invoke(test: () -> Unit): TestCaseDescriptor {
    val descriptor = TestCaseDescriptor(specDescriptor.uniqueId.append("test", this), this, source, this@StringSpec, test, defaultTestCaseConfig)
    specDescriptor.addChild(descriptor)
    return descriptor
  }
}