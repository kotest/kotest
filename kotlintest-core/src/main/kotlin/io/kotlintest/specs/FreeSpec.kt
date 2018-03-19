package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer

abstract class FreeSpec(body: FreeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  infix operator fun String.minus(init: FreeSpecScope.() -> Unit) {
    val descriptor = TestContainer(this, this@FreeSpec)
    rootContainer.addContainer(descriptor)
    FreeSpecScope(descriptor).init()
  }

  inner class FreeSpecScope(private val parentDescriptor: TestContainer) {
    infix operator fun String.invoke(test: () -> Unit): TestCase {
      val testcase = TestCase(this, this@FreeSpec, test, defaultTestCaseConfig)
      parentDescriptor.addTest(testcase)
      return testcase
    }
  }
}