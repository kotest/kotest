package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

abstract class ExpectSpec(body: ExpectSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun context(name: String, init: ExpectSpecScope.() -> Unit) {
    val descriptor = TestScope("Context: $name", this@ExpectSpec)
    rootContainer.addScope(descriptor)
    ExpectSpecScope(descriptor).init()
  }

  inner class ExpectSpecScope(private val parentDescriptor: TestScope) {

    fun context(name: String, init: ExpectSpecScope.() -> Unit) {
      val descriptor = TestScope("Context: $name", this@ExpectSpec)
      parentDescriptor.addScope(descriptor)
      ExpectSpecScope(descriptor).init()
    }

    fun expect(name: String, test: () -> Unit): TestCase {
      val tc = TestCase("Expect: $name", this@ExpectSpec, test, defaultTestCaseConfig)
      parentDescriptor.addTest(tc)
      return tc
    }
  }
}