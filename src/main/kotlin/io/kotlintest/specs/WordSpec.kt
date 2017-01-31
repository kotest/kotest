package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import java.util.*

abstract class WordSpec : TestBase() {

  companion object {
    data class SpecDef(val name: String, val annotations: List<Annotation> = emptyList())
  }

  var current = root

  operator fun String.invoke(vararg annotations: Annotation = emptyArray()) = this(annotations.toList())
  operator fun String.invoke(annotations: List<Annotation> = emptyList()) = SpecDef(this, annotations)

  infix fun String.should(init: () -> Unit) = SpecDef(this).should(init)

  infix fun SpecDef.should(init: () -> Unit): Unit {
    val suite = TestSuite(name, ArrayList<TestSuite>(), ArrayList<TestCase>(), annotations)
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  operator fun String.invoke(vararg annotations: Annotation = emptyArray(), test: () -> Unit): TestCase = this(annotations.toList(), test)
  operator fun String.invoke(annotations: List<Annotation> = emptyList(), test: () -> Unit): TestCase {
    val testCase = TestCase(
        suite = current, name = "should " + this, test = test, config = defaultTestCaseConfig.copy(), annotations = annotations)
    current.cases.add(testCase)
    return testCase
  }
}