package io.kotlintest

import java.util.*

abstract class FreeSpec : TestBase() {

  var current = root

  infix operator fun String.minus(init: () -> Unit): Unit {
    val suite = TestSuite.empty(this)
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

data class TestSuite(val name: String, val suites: MutableList<TestSuite>, val cases: MutableList<TestCase>) {
  companion object {
    fun empty(name: String) = TestSuite(name, ArrayList<TestSuite>(), ArrayList<TestCase>())
  }
}

data class TestCase(val name: String, val test: () -> Unit)