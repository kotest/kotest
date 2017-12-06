package io.kotlintest.specs

import io.kotlintest.Spec
import io.kotlintest.TestCaseDescriptor

abstract class FunSpec(body: FunSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  fun test(name: String, test: () -> Unit): TestCaseDescriptor {
    val testcase = TestCaseDescriptor(specDescriptor.uniqueId.append("test", name), "should " + name, source, this@FunSpec, test, defaultTestCaseConfig)
    specDescriptor.addChild(testcase)
    return testcase
  }
}