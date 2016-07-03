package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestSuite

abstract class ShouldSpec : TestBase() {

  var current = root

  operator fun String.invoke(init: () -> Unit): Unit {
    val suite = TestSuite.empty(this)
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun should(name: String, test: () -> Unit): TestCase {
    val testCase = TestCase(
        suite = current, name = "should $name", test = test, config = defaultTestCaseConfig)
    current.cases.add(testCase)
    return testCase
  }
}