package io.kotlintest.specs

import io.kotlintest.*
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class) // required to let IntelliJ discover tests
abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  fun Given(description: String, init: Given.() -> Unit): Unit = given(description, init)
  fun given(description: String, init: Given.() -> Unit): Unit {
    val suite = TestSuite("Given ${sanitizeSpecName(description)}")
    rootTestSuite.addNestedSuite(suite)
    val given = Given(suite)
    given.init()
  }

  inner class Given(val nestedSuite: TestSuite) {
    fun `When`(description: String, init: When.() -> Unit): Unit = `when`(description, init)
    fun `when`(description: String, init: When.() -> Unit): Unit {
      val suite = TestSuite("When ${sanitizeSpecName(description)}")
      nestedSuite.addNestedSuite(suite)
      val `when` = When(suite)
      `when`.init()
    }
  }

  inner class When(val nestedSuite: TestSuite) {
    fun Then(description: String, check: () -> Unit): TestCase = then(description, check)
    fun then(description: String, check: () -> Unit): TestCase {
      val testCase =
          TestCase(
              nestedSuite,
              "Then ${sanitizeSpecName(description)}",
              check,
              defaultTestCaseConfig)
      nestedSuite.addTestCase(testCase)
      return testCase
    }
  }
}
