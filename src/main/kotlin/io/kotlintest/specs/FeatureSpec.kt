package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import java.util.*

abstract class FeatureSpec : TestBase() {

  var current = root

  fun feature(name: String, vararg annotations: Annotation = emptyArray(), init: () -> Unit) = feature(name, annotations.toList(), init)
  fun feature(name: String, annotations: List<Annotation>, init: () -> Unit): Unit {
    val suite = TestSuite("Feature: $name", ArrayList<TestSuite>(), ArrayList<TestCase>(), annotations)
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun scenario(name: String, vararg annotations: Annotation = emptyArray(), test: () -> Unit): TestCase = scenario(name, annotations.toList(), test)
  fun scenario(name: String, annotations: List<Annotation> = emptyList(), test: () -> Unit): TestCase {
    val tc = TestCase(current, "Scenario: $name", test, defaultTestCaseConfig, annotations)
    current.cases.add(tc)
    return tc
  }

}