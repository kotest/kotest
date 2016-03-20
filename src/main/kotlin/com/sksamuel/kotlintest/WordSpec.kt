package com.sksamuel.kotlintest

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

  infix fun String.with(test: () -> Unit): Unit {
    current.cases.add(TestCase(this, test))
  }
}