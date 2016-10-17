package io.kotlintest.specs

import io.kotlintest.Spec
import io.kotlintest.TestCase

abstract class StringSpec(body: StringSpec.() -> Unit = {}) : Spec() {
  init { body() }

  operator fun String.invoke(test: () -> Unit): TestCase {
    val tc = TestCase(suite = root, name = this, test = test, config = defaultTestCaseConfig)
    root.cases.add(tc)
    return tc
  }
}