package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

abstract class ExpectSpec(body: ExpectSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun context(name: String, init: ExpectSpecScope.() -> Unit) {
    val scope = TestScope("Context: $name", this@ExpectSpec, { ExpectSpecScope(TestScope.empty()).init() })
    rootScope.addScope(scope)
    ExpectSpecScope(scope).init()
  }

  inner class ExpectSpecScope(private val parentScope: TestScope) {

    fun context(name: String, init: ExpectSpecScope.() -> Unit) {
      val descriptor = TestScope("Context: $name", this@ExpectSpec, { ExpectSpecScope(TestScope.empty()).init() })
      parentScope.addScope(descriptor)
      ExpectSpecScope(descriptor).init()
    }

    fun expect(name: String, test: () -> Unit): TestCase {
      val tc = TestCase("Expect: $name", this@ExpectSpec, test, defaultTestCaseConfig)
      parentScope.addTest(tc)
      return tc
    }
  }
}