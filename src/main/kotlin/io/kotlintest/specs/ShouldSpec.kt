package io.kotlintest.specs

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class) // required to let IntelliJ discover tests
abstract class ShouldSpec(body: ShouldSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  private var current = root

  operator fun String.invoke(init: () -> Unit): Unit {
    val suite = TestSuite(sanitizeSpecName(this))
    current.addNestedSuite(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun should(name: String, test: () -> Unit): TestCase {
    val testCase = TestCase(
        suite = current, name = "should $name", test = test, config = defaultTestCaseConfig)
    current.addTestCase(testCase)
    return testCase
  }
}