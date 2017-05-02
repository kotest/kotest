package io.kotlintest.specs

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class) // required to let IntelliJ discover tests
abstract class WordSpec(body: WordSpec.() -> Unit = {}) : Spec() {

  companion object {
    data class SpecDef(val name: String, val annotations: List<Annotation> = emptyList())
  }

  init {
    body()
  }

  private var current = rootTestSuite

  operator fun String.invoke(vararg annotations: Annotation = emptyArray()) = this(annotations.toList())
  operator fun String.invoke(annotations: List<Annotation> = emptyList()) = SpecDef(this, annotations)

  infix fun String.should(init: () -> Unit) = SpecDef(this).should(init)

  infix fun SpecDef.should(init: () -> Unit): Unit {
    val suite = TestSuite(sanitizeSpecName(name), annotations)
    current.addNestedSuite(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  operator fun String.invoke(vararg annotations: Annotation = emptyArray(), test: () -> Unit): TestCase = this(annotations.toList(), test)
  operator fun String.invoke(annotations: List<Annotation>, test: () -> Unit): TestCase {
    val testCase = TestCase(
        suite = current, name = "should " + this, test = test, config = defaultTestCaseConfig.copy(), annotations = annotations)
    current.addTestCase(testCase)
    return testCase
  }
}
