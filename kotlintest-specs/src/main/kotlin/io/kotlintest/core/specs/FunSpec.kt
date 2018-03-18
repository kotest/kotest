package io.kotlintest.core.specs

import io.kotlintest.core.AbstractSpec
import io.kotlintest.core.TestCase

abstract class FunSpec(body: FunSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun test(name: String, test: () -> Unit): TestCase {
    val testcase = TestCase(name, this@FunSpec, specDescriptor, test, defaultTestCaseConfig)
    specDescriptor.addTest(testcase)
    return testcase
  }
}