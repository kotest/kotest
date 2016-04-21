package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestSuite

abstract class FreeSpec : TestBase() {

  var current = root

  infix operator fun String.minus(init: () -> Unit): Unit {
    val suite = TestSuite.empty(this)
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  infix fun String.with(test: () -> Unit): Unit {
    current.cases.add(TestCase(current, this, test))
  }

  infix operator fun String.invoke(test: () -> Unit): Unit {
    current.cases.add(TestCase(current, this, test))
  }
}