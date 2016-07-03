package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import java.util.*

abstract class WordSpec : TestBase() {

  var current = root

  infix fun String.should(init: () -> Unit): Unit {
    val suite = TestSuite(this, ArrayList<TestSuite>(), ArrayList<TestCase>())
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  infix operator fun String.invoke(test: () -> Unit): TestCase {
    val testCase = TestCase(
        suite = current, name = "should " + this, test = test, config = defaultTestCaseConfig.copy())
    current.cases.add(testCase)
    return testCase
  }
}