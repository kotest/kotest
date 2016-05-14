package io.kotlintest.specs

import io.kotlintest.TestCase
import io.kotlintest.TestConfig
import io.kotlintest.TestSuite
import io.kotlintest.properties.PropertyTesting
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.getOrPut

abstract class FlatSpec : PropertyTesting() {

  protected val suites: MutableMap<String, TestSuite> = HashMap()

  fun String.config(invocations: Int = 1,
                    ignored: Boolean = false,
                    timeout: Long = 0,
                    timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
                    threads: Int = 1,
                    tag: String? = null,
                    tags: List<String> = listOf()): Pair<String, TestConfig> =
      Pair(this, TestConfig(ignored, invocations, timeout, timeoutUnit, threads, tags))

  // allows us to write "name of test" { test here }
  operator fun String.invoke(test: () -> Unit): Pair<String, () -> Unit> = Pair(this, test)

  // combines the suite name and "name of test" with the keyword should
  infix fun Pair<String, TestConfig>.should(pair: Pair<String, () -> Unit>): Unit {
    val (testName, test) = pair
    val suite = suites.getOrPut(this.first, {
      val suite = TestSuite.empty(this.first)
      root.nestedSuites.add(suite)
      suite
    })
    suite.cases.add(TestCase(suite, testName, test, this.second))
  }

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