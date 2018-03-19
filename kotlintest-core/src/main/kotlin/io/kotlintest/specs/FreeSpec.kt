package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer

abstract class FreeSpec(body: FreeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  infix operator fun String.minus(init: FreeSpecScope.() -> Unit) {
    val descriptor = TestContainer(this)
    rootContainer.addContainer(descriptor)
    FreeSpecScope(descriptor).init()
  }

  inner class FreeSpecScope(private val parentDescriptor: TestContainer) {
    infix operator fun String.invoke(test: () -> Unit): TestCase {
      val testcase = TestCase(this, nextId(), this@FreeSpec, parentDescriptor, test, defaultTestCaseConfig)
      parentDescriptor.addTest(testcase)
      return testcase
    }
  }
}