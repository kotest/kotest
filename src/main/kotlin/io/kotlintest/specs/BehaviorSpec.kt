package io.kotlintest.specs

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class) // required to let IntelliJ discover tests
abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : Spec() {
  init { body() }

  private var current = root

  fun Given(name: String, init: () -> Unit): Unit = given(name, init)

  fun given(name: String, init: () -> Unit): Unit {
    val suite = TestSuite("Given ${sanitizeSpecName(name)}")
    current.addNestedSuite(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun When(name: String, init: () -> Unit): Unit = `when`(name, init)

  fun `when`(name: String, init: () -> Unit): Unit {
    val suite = TestSuite("When ${sanitizeSpecName(name)}")
    current.addNestedSuite(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun Then(name: String, test: () -> Unit): Unit = then(name, test)

  fun then(name: String, test: () -> Unit): Unit {
    val testCase = TestCase(current, "Then ${sanitizeSpecName(name)}", test, defaultTestCaseConfig)
    current.addTestCase(testCase)
  }
}