package io.kotlintest.specs

import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import io.kotlintest.properties.PropertyTesting
import java.util.*

abstract class BehaviorSpec : PropertyTesting() {

  var current = root

  fun given(name: String, init: () -> Unit): Unit {
    val suite = TestSuite("Given $name", ArrayList<TestSuite>(), ArrayList<TestCase>())
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun `when`(name: String, init: () -> Unit): Unit {
    val suite = TestSuite("When $name", ArrayList<TestSuite>(), ArrayList<TestCase>())
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun then(name: String, test: () -> Unit): Unit {
    current.cases.add(TestCase(current, "Then $name", test))
  }
}