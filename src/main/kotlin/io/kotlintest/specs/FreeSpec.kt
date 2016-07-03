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

  infix operator fun String.invoke(test: () -> Unit): TestCase {
    val tc = TestCase(suite = current, name = this, test = test, config = defaultTestCaseConfig)
    current.cases.add(tc)
    return tc
  }
}