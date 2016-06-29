package io.kotlintest.specs

import io.kotlintest.TestCase
import io.kotlintest.properties.PropertyTesting

abstract class FunSpec : PropertyTesting() {

  fun test(name: String, test: () -> Unit): TestCase {
    val tc = TestCase(suite = root, name = name, test = test, config = defaultTestCaseConfig)
    root.cases.add(tc)
    return tc
  }
}