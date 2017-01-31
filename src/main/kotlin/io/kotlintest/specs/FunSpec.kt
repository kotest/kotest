package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase

abstract class FunSpec : TestBase() {

  fun test(name: String, vararg annotations: Annotation = emptyArray(), test: () -> Unit) = test(name, annotations.toList(), test)
  fun test(name: String, annotations: List<Annotation> = emptyList(), test: () -> Unit): TestCase {
    val tc = TestCase(suite = root, name = name, test = test, config = defaultTestCaseConfig, annotations = annotations)
    root.cases.add(tc)
    return tc
  }
}