package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase

abstract class StringSpec : TestBase() {

  operator fun String.invoke(vararg annotations: Annotation = emptyArray(), test: () -> Unit): TestCase = this(annotations.toList(), test)
  operator fun String.invoke(annotations: List<Annotation> = emptyList(), test: () -> Unit): TestCase {
    val tc = TestCase(suite = root, name = this, test = test, config = defaultTestCaseConfig, annotations = annotations)
    root.cases.add(tc)
    return tc
  }
}