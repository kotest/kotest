package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import java.util.*

abstract class FeatureSpec : TestBase() {

  var current = root

  fun feature(name: String, init: () -> Unit): Unit {
    val suite = TestSuite("Feature: $name", ArrayList<TestSuite>(), ArrayList<TestCase>())
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun scenario(name: String, test: () -> Unit): TestCase {
    val tc = TestCase(current, "Scenario: $name", test, defaultTestCaseConfig)
    current.cases.add(tc)
    return tc
  }

}