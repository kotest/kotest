package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestSuite

abstract class FreeSpec : TestBase() {

  companion object {
    data class Spec(val name: String, val annotations: List<Annotation>)
  }

  var current = root

  operator fun String.invoke(vararg annotations: Annotation = emptyArray()) = this(annotations.toList())
  operator fun String.invoke(annotations: List<Annotation> = emptyList()) = Spec(this, annotations)

  infix operator fun String.minus(init: () -> Unit): Unit {
    val suite = TestSuite.empty(this.replace("(", " ").replace(")", " "))
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  infix operator fun Spec.minus(init: () -> Unit): Unit {
    val suite = TestSuite.empty(name.replace("(", " ").replace(")", " ")).copy(annotations = annotations)
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  operator fun String.invoke(vararg annotations: Annotation = emptyArray(), test: () -> Unit): TestCase = this(annotations.toList(), test)
  operator fun String.invoke(annotations: List<Annotation> = emptyList(), test: () -> Unit): TestCase {
    val tc = TestCase(suite = current, name = this.replace("(", " ").replace(")", " "), test = test, config = defaultTestCaseConfig, annotations = annotations)
    current.cases.add(tc)
    return tc
  }
}