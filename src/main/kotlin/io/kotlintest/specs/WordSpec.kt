package io.kotlintest.specs

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import org.junit.runner.RunWith
import java.util.*

@RunWith(KTestJUnitRunner::class) // required to let IntelliJ discover tests
abstract class WordSpec(body: WordSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  var current = root

  infix fun String.should(init: () -> Unit): Unit {
    val suite = TestSuite(sanitizeSpecName(this))
    current.addNestedSuite(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  infix operator fun String.invoke(test: () -> Unit): TestCase {
    val testCase = TestCase(
        suite = current, name = "should " + this, test = test, config = defaultTestCaseConfig.copy())
    current.addTestCase(testCase)
    return testCase
  }
}