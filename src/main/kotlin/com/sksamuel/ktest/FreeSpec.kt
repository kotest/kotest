package com.sksamuel.ktest

import java.util.*

abstract class FreeSpec : TestBase() {

  var current = root

  infix operator fun String.minus(init: TestSuite.() -> Unit): Unit {
    val suite = TestSuite(this, ArrayList<TestSuite>(), ArrayList<TestCase>())
    current.suites.add(suite)
    val temp = current
    current = suite
    current.init()
    current = temp
  }

  infix fun String.with(test: () -> Unit): Unit {
    current.cases.add(TestCase(this, test))
  }
}

abstract class TestBase {
  val root = TestSuite("root", ArrayList<TestSuite>(), ArrayList<TestCase>())
}

data class TestSuite(val name: String, val suites: MutableList<TestSuite>, val cases: MutableList<TestCase>)
data class TestCase(val name: String, val test: () -> Unit)