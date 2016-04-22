package io.kotlintest.specs

import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import io.kotlintest.properties.PropertyTesting
import java.util.*

abstract class WordSpec : PropertyTesting() {

  var current = root

  infix fun String.should(init: () -> Unit): Unit {
    val suite = TestSuite(this, ArrayList<TestSuite>(), ArrayList<TestCase>())
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  infix operator fun String.invoke(test: () -> Unit): Unit {
    current.cases.add(TestCase(current, "should " + this, test))
  }
}