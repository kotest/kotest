package com.sksamuel.ktest

import java.util.*
import kotlin.collections.getOrPut

abstract class FlatSpec : TestBase() {

  val suites: MutableMap<String, TestSuite> = HashMap()

  infix fun String.should(msg: String): TestBuilder = TestBuilder(this, msg)

  inner class TestBuilder(val suiteName: String, val testName: String) {
    infix fun with(test: () -> Unit): Unit {
      val suite = suites.getOrPut(suiteName, {
        val suite = TestSuite.empty(suiteName)
        root.suites.add(suite)
        suite
      })
      suite.cases.add(TestCase(testName, test))
    }
  }
}
