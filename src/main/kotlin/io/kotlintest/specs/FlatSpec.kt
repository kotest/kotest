package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import java.util.*
import kotlin.collections.getOrPut

abstract class FlatSpec : TestBase() {

  val suites: MutableMap<String, TestSuite> = HashMap()

  infix fun String.should(msg: String): TestBuilder = TestBuilder(this, msg)

  class TestHolder(val test: () -> Unit)

  infix operator fun String.invoke(test: () -> Unit): () -> Unit = test

  infix fun String.should(test: () -> Unit): Unit {
    val suite = suites.getOrPut(this, {
      val suite = TestSuite.empty(this)
      root.suites.add(suite)
      suite
    })
    suite.cases.add(TestCase(this, test))
  }

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
