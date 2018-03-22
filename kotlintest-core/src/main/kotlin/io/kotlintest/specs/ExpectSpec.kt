package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

abstract class ExpectSpec(body: ExpectSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  fun context(name: String, init: ExpectSpecScope.() -> Unit) {
    rootScope.addContainer("Context: $name", this@ExpectSpec, ::ExpectSpecScope, init)
  }

  inner class ExpectSpecScope : TestScope() {

    fun context(name: String, init: ExpectSpecScope.() -> Unit) =
        addContainer("Context: $name", this@ExpectSpec, ::ExpectSpecScope, init)

    fun expect(name: String, test: () -> Unit): TestCase =
        addTest("Expect: $name", this@ExpectSpec, test, defaultTestCaseConfig)
  }
}