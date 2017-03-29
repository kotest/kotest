package io.kotlintest.specs

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.TestCase
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class) // required to let IntelliJ discover tests
abstract class FunSpec(body: FunSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  fun test(name: String, vararg annotations: Annotation = emptyArray(), test: () -> Unit) = test(name, annotations.toList(), test)
  fun test(name: String, annotations: List<Annotation> = emptyList(), test: () -> Unit): TestCase {
    val tc = TestCase(suite = rootTestSuite, name = name, test = test, config = defaultTestCaseConfig, annotations = annotations)
    rootTestSuite.addTestCase(tc)
    return tc
  }
}