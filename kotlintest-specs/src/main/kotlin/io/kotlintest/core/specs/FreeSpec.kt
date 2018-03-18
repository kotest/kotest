package io.kotlintest.core.specs

import io.kotlintest.core.AbstractSpec
import io.kotlintest.core.TestCase
import io.kotlintest.core.TestCaseDescriptor

abstract class FreeSpec(body: FreeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  infix operator fun String.minus(init: FreeSpecScope.() -> Unit) {
    val descriptor = TestCaseDescriptor(this)
    specDescriptor.addDescriptor(descriptor)
    FreeSpecScope(descriptor).init()
  }

  inner class FreeSpecScope(private val parentDescriptor: TestCaseDescriptor) {
    infix operator fun String.invoke(test: () -> Unit): TestCase {
      val testcase = TestCase(this, this@FreeSpec, parentDescriptor, test, defaultTestCaseConfig)
      parentDescriptor.addTest(testcase)
      return testcase
    }
  }
}