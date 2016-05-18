package io.kotlintest.specs

import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import io.kotlintest.properties.PropertyTesting

abstract class FreeSpec : PropertyTesting() {

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
    val tc = TestCase(current, this, test)
    current.cases.add(tc)
    return tc
  }
}