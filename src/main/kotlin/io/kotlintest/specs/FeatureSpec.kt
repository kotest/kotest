package io.kotlintest.specs

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class) // required to let IntelliJ discover tests
abstract class FeatureSpec(body: FeatureSpec.() -> Unit = {}) : Spec() {

  private var current = rootTestSuite

  init {
    body()
  }

  fun feature(name: String, init: () -> Unit): Unit {
    val suite = TestSuite("Feature: ${sanitizeSpecName(name)}")
    current.addNestedSuite(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun scenario(name: String, test: () -> Unit): TestCase {
    val tc = TestCase(current, "Scenario: ${sanitizeSpecName(name)}", test, defaultTestCaseConfig)
    current.addTestCase(tc)
    return tc
  }

}