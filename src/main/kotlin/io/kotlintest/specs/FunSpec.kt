package io.kotlintest.specs

import io.kotlintest.TestCase
import io.kotlintest.properties.PropertyTesting

abstract class FunSpec : PropertyTesting() {

  fun test(name: String, test: () -> Unit): Unit {
    root.cases.add(TestCase(root, name, test))
  }
}