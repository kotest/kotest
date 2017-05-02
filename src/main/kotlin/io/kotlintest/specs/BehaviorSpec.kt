package io.kotlintest.specs

import io.kotlintest.*
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class) // required to let IntelliJ discover tests
abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  fun Given(name: String, vararg annotations: Annotation = emptyArray(), init: Given.() -> Unit): Unit = given(name, annotations.toList(), init)
  fun Given(name: String, annotations: List<Annotation> = emptyList(), init: Given.() -> Unit): Unit = given(name, annotations, init)
  fun given(name: String, vararg annotations: Annotation = emptyArray(), init: Given.() -> Unit): Unit = given(name, annotations.toList(), init)
  fun given(description: String, annotations: List<Annotation> = emptyList(), init: Given.() -> Unit): Unit {
    val suite = TestSuite("Given ${sanitizeSpecName(description)}", annotations)
    rootTestSuite.addNestedSuite(suite)
    val given = Given(suite)
    given.init()
  }

  inner class Given(val nestedSuite: TestSuite) {
    fun When(name: String, vararg annotations: Annotation = emptyArray(), init: When.() -> Unit): Unit = `when`(name, annotations.toList(), init)
    fun When(name: String, annotations: List<Annotation> = emptyList(), init: When.() -> Unit): Unit = `when`(name, annotations, init)
    fun `when`(name: String, vararg annotations: Annotation = emptyArray(), init: When.() -> Unit): Unit = `when`(name, annotations.toList(), init)
    fun `when`(description: String, annotations: List<Annotation> = emptyList(), init: When.() -> Unit): Unit {
      val suite = TestSuite("When ${sanitizeSpecName(description)}", annotations)
      nestedSuite.addNestedSuite(suite)
      val `when` = When(suite)
      `when`.init()
    }
  }

  inner class When(val nestedSuite: TestSuite) {
    fun Then(name: String, vararg annotations : Annotation = emptyArray(), check: () -> Unit) = then(name, annotations.toList(), check)
    fun Then(name: String, annotations: List<Annotation> = emptyList(), check: () -> Unit) = then(name, annotations, check)
    fun then(name: String, vararg annotations:Annotation = emptyArray(), check: () -> Unit) = then(name, annotations.toList(), check)
    fun then(description: String, annotations: List<Annotation>, check: () -> Unit): TestCase {
      val testCase =
          TestCase(
              nestedSuite,
              "Then ${sanitizeSpecName(description)}",
              check,
              defaultTestCaseConfig,
              annotations)
      nestedSuite.addTestCase(testCase)
      return testCase
    }
  }
}
