package io.kotlintest.specs

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class) // required to let IntelliJ discover tests
abstract class FeatureSpec(body: FeatureSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  private var current = rootTestSuite

  fun feature(name: String, vararg annotations: Annotation = emptyArray(), init: () -> Unit) = feature(name, annotations.toList(), init)
  fun feature(name: String, annotations: List<Annotation> = emptyList(), init: () -> Unit): Unit {
    val suite = TestSuite("Feature: ${sanitizeSpecName(name)}", annotations)
    current.addNestedSuite(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun scenario(name: String, vararg annotations: Annotation = emptyArray(), test: () -> Unit): TestCase = scenario(name, annotations.toList(), test)
  fun scenario(name: String, annotations: List<Annotation> = emptyList(), test: () -> Unit): TestCase {
    val tc = TestCase(current, "Scenario: ${sanitizeSpecName(name)}", test, defaultTestCaseConfig, annotations)
    current.addTestCase(tc)
    return tc
  }

}