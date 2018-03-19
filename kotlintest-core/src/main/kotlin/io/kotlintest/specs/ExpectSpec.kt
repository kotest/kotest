package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer

abstract class ExpectSpec(body: ExpectSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun context(name: String, init: ExpectSpecScope.() -> Unit) {
    val descriptor = TestContainer("Context: $name", this@ExpectSpec)
    rootContainer.addContainer(descriptor)
    ExpectSpecScope(descriptor).init()
  }

  inner class ExpectSpecScope(private val parentDescriptor: TestContainer) {
    fun expect(name: String, test: () -> Unit): TestCase {
      val tc = TestCase("Expect: $name", this@ExpectSpec, test, defaultTestCaseConfig)
      parentDescriptor.addTest(tc)
      return tc
    }
  }
}