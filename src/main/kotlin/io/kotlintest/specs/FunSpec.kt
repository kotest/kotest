package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase

abstract class FunSpec(body: FunSpec.() -> Unit = {}) : TestBase() {
  init { body(this) }

  fun test(name: String, test: () -> Unit): TestCase {
    val tc = TestCase(suite = root, name = name, test = test, config = defaultTestCaseConfig)
    root.cases.add(tc)
    return tc
  }
}