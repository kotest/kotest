package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase

abstract class FunSpec : TestBase() {

  fun test(name: String, test: () -> Unit): Unit {
    root.cases.add(TestCase(root, name, test))
  }
}