package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import java.util.*

abstract class WordSpec : TestBase() {

  var current = root

  infix fun String.should(init: () -> Unit): Unit {
    val suite = TestSuite(this, ArrayList<TestSuite>(), ArrayList<TestCase>())
    current.suites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  infix operator fun String.invoke(test: () -> Unit): Unit = with(test)

  infix fun String.with(test: () -> Unit): Unit {
    current.cases.add(TestCase(this, test))
  }
}