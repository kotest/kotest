package io.kotlintest.specs

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestSuite
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class) // required to let IntelliJ discover tests
abstract class FreeSpec(body: FreeSpec.() -> Unit = {}) : Spec() {

  companion object {
    data class SpecDef(val name: String, val annotations: List<Annotation> = emptyList())
  }

  init {
    body()
  }

  private var current = rootTestSuite

  operator fun String.invoke(vararg annotations: Annotation = emptyArray()) = this(annotations.toList())
  operator fun String.invoke(annotations: List<Annotation> = emptyList()) = SpecDef(this, annotations)

  infix operator fun String.minus(init: () -> Unit): Unit = SpecDef(this) - init
  infix operator fun SpecDef.minus(init: () -> Unit): Unit {
    val suite = TestSuite(sanitizeSpecName(name), annotations)
    current.addNestedSuite(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  operator fun String.invoke(vararg annotations: Annotation = emptyArray(), test: () -> Unit): TestCase = this(annotations.toList(), test)
  operator fun String.invoke(annotations: List<Annotation> = emptyList(), test: () -> Unit): TestCase {
    val tc = TestCase(
        suite = current,
        name = sanitizeSpecName(this),
        test = test,
        config = defaultTestCaseConfig,
        annotations = annotations
    )
    current.addTestCase(tc)
    return tc
  }
}
