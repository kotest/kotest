package io.kotlintest.specs

import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import io.kotlintest.properties.PropertyTesting
import java.util.*

abstract class FeatureSpec : PropertyTesting() {

  var current = root

  fun feature(name: String, init: () -> Unit): Unit {
    val suite = TestSuite("Feature: $name", ArrayList<TestSuite>(), ArrayList<TestCase>())
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun scenario(name: String, test: () -> Unit): Unit {
    current.cases.add(TestCase(current, "Scenario: $name", test))
  }

}