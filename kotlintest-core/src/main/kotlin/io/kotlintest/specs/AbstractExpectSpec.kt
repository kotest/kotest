package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

abstract class AbstractExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  fun context(name: String, init: ExpectSpecScope.() -> Unit) {
    rootScope.addContainer("Context: $name", this@AbstractExpectSpec, ::ExpectSpecScope, init)
  }

  inner class ExpectSpecScope : TestScope() {

    fun context(name: String, init: ExpectSpecScope.() -> Unit) =
        addContainer("Context: $name", this@AbstractExpectSpec, ::ExpectSpecScope, init)

    fun expect(name: String, test: () -> Unit): TestCase =
        addTest("Expect: $name", this@AbstractExpectSpec, test, defaultTestCaseConfig)
  }
}