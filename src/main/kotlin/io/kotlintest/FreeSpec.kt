package io.kotlintest

import java.util.*

data class TestSuite(val name: String, val suites: MutableList<TestSuite>, val cases: MutableList<io.kotlintest.TestCase>) {
  companion object {
    fun empty(name: String) = TestSuite(name, ArrayList<TestSuite>(), ArrayList<io.kotlintest.TestCase>())
  }
}

data class TestCase(val name: String, val test: () -> Unit)