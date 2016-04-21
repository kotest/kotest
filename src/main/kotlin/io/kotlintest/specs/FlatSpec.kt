package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import java.util.*
import kotlin.collections.getOrPut

abstract class FlatSpec : TestBase() {

  protected val suites: MutableMap<String, TestSuite> = HashMap()

  // allows us to write "name of test" { test here }
  operator fun String.invoke(test: () -> Unit): Pair<String, () -> Unit> = Pair(this, test)

  // combines the suite name and "name of test" with the keyword should
  infix fun String.should(pair: Pair<String, () -> Unit>): Unit {
    val suiteName = this
    val (testName, test) = pair
    val suite = suites.getOrPut(suiteName, {
      val suite = TestSuite.empty(suiteName)
      root.nestedSuites.add(suite)
      suite
    })
    suite.cases.add(TestCase(suite, testName, test))
  }
}
