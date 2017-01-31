package io.kotlintest.specs

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import java.util.*

abstract class BehaviorSpec : TestBase() {

  var current = root

  fun Given(name: String, vararg annotations: Annotation = emptyArray(), init: () -> Unit): Unit = given(name, annotations.toList(), init)
  fun Given(name: String, annotations: List<Annotation> = emptyList(), init: () -> Unit): Unit = given(name, annotations, init)
  fun given(name: String, vararg annotations: Annotation = emptyArray(), init: () -> Unit): Unit = given(name, annotations.toList(), init)
  fun given(name: String, annotations: List<Annotation> = emptyList(), init: () -> Unit): Unit {
    val suite = TestSuite("Given $name", ArrayList<TestSuite>(), ArrayList<TestCase>(), annotations)
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun When(name: String, vararg annotations: Annotation = emptyArray(), init: () -> Unit): Unit = `when`(name, annotations.toList(), init)
  fun When(name: String, annotations: List<Annotation> = emptyList(), init: () -> Unit): Unit = `when`(name, annotations, init)
  fun `when`(name: String, vararg annotations: Annotation = emptyArray(), init: () -> Unit): Unit = `when`(name, annotations.toList(), init)
  fun `when`(name: String, annotations: List<Annotation> = emptyList(), init: () -> Unit): Unit {
    val suite = TestSuite("When $name", ArrayList<TestSuite>(), ArrayList<TestCase>(), annotations)
    current.nestedSuites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun Then(name: String, vararg annotations : Annotation = emptyArray(), test: () -> Unit): Unit = then(name, annotations.toList(), test)
  fun Then(name: String, annotations: List<Annotation> = emptyList(), test: () -> Unit): Unit = then(name, annotations, test)
  fun then(name: String, vararg annotations:Annotation = emptyArray(), test: () -> Unit): Unit = then(name, annotations.toList(), test)
  fun then(name: String, annotations: List<Annotation> = emptyList(), test: () -> Unit): Unit{
    current.cases.add(TestCase(current, "Then $name", test, defaultTestCaseConfig, annotations))
  }
}