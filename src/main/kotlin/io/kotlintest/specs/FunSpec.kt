package io.kotlintest.specs

import io.kotlintest.Spec
import io.kotlintest.TestCase

abstract class FunSpec(body: FunSpec.() -> Unit = {}) : Spec() {
  init { body() }

  fun test(name: String, test: () -> Unit): TestCase {
    val tc = TestCase(suite = root, name = name, test = test, config = defaultTestCaseConfig)
    root.cases.add(tc)
    return tc
  }
}