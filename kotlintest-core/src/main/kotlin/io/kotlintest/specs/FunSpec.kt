package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase

abstract class FunSpec(body: FunSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun test(name: String, test: () -> Unit): TestCase {
    val testcase = TestCase(name, nextId(), this@FunSpec, rootContainer, test, defaultTestCaseConfig)
    rootContainer.addTest(testcase)
    return testcase
  }
}