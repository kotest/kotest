package io.kotlintest.specs

import io.kotlintest.TestCase
import io.kotlintest.properties.PropertyTesting

abstract class FunSpec : PropertyTesting() {

  fun test(name: String, test: () -> Unit): TestCase {
    val tc = TestCase(root, name, test)
    root.cases.add(tc)
    return tc
  }
}