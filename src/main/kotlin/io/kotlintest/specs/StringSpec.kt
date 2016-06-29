package io.kotlintest.specs

import io.kotlintest.TestCase
import io.kotlintest.properties.PropertyTesting

abstract class StringSpec : PropertyTesting() {

  operator fun String.invoke(test: () -> Unit): TestCase {
    val tc = TestCase(suite = root, name = this, test = test, config = defaultTestCaseConfig)
    root.cases.add(tc)
    return tc
  }
}