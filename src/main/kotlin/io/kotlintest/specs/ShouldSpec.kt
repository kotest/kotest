package io.kotlintest.specs

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class) // required to let IntelliJ discover tests
abstract class ShouldSpec(body: ShouldSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  private var current = rootTestSuite

  operator fun String.invoke(vararg annotations: Annotation = emptyArray(), init: () -> Unit): Unit = this(annotations.toList(), init)
  operator fun String.invoke(annotations: List<Annotation>, init: () -> Unit): Unit {
    val suite = TestSuite(sanitizeSpecName(this), annotations)
    current.addNestedSuite(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun should(name: String, vararg annotations: Annotation = emptyArray(), test: () -> Unit): TestCase = should(name, annotations.toList(), test)
  fun should(name: String, annotations: List<Annotation>, test: () -> Unit): TestCase {
    val testCase = TestCase(
        suite = current, name = "should $name", test = test, config = defaultTestCaseConfig, annotations = annotations)
    current.addTestCase(testCase)
    return testCase
  }
}