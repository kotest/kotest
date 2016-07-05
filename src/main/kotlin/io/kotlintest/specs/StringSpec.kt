package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase

abstract class StringSpec(body: StringSpec.() -> Unit = {}) : TestBase() {
  init { body(this) }

  operator fun String.invoke(test: () -> Unit): TestCase {
    val tc = TestCase(suite = root, name = this, test = test, config = defaultTestCaseConfig)
    root.cases.add(tc)
    return tc
  }
}