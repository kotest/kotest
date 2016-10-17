package io.kotlintest.specs

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import java.util.*

abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : Spec() {
  init { body() }

  var current = root

  fun Given(name: String, init: () -> Unit): Unit = given(name, init)
  fun given(name: String, init: () -> Unit): Unit {
    val suite = TestSuite("Given $name", ArrayList<TestSuite>(), ArrayList<TestCase>())
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun When(name: String, init: () -> Unit): Unit = `when`(name, init)
  fun `when`(name: String, init: () -> Unit): Unit {
    val suite = TestSuite("When $name", ArrayList<TestSuite>(), ArrayList<TestCase>())
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun Then(name: String, test: () -> Unit): Unit = then(name, test)
  fun then(name: String, test: () -> Unit): Unit {
    current.cases.add(TestCase(current, "Then $name", test, defaultTestCaseConfig))
  }
}