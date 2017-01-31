package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestSuite

abstract class ShouldSpec : TestBase() {

  var current = root

  operator fun String.invoke(vararg annotations: Annotation = emptyArray(), init: () -> Unit): Unit = this(annotations.toList(), init)
  operator fun String.invoke(annotations: List<Annotation> = emptyList(), init: () -> Unit): Unit {
    val suite = TestSuite.empty(this).copy(annotations = annotations)
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun should(name: String, vararg annotations: Annotation = emptyArray(), test: () -> Unit): TestCase = should(name, annotations.toList(), test)
  fun should(name: String, annotations: List<Annotation>, test: () -> Unit): TestCase {
    val testCase = TestCase(
        suite = current, name = "should $name", test = test, config = defaultTestCaseConfig, annotations = annotations)
    current.cases.add(testCase)
    return testCase
  }
}