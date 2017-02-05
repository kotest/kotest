package io.kotlintest.specs

import io.kotlintest.*
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class) // required to let IntelliJ discover tests
abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  fun given(description: String, init: Given.() -> Unit): Given {
    val suite = TestSuite("Given ${sanitizeSpecName(description)}")
    rootTestSuite.addNestedSuite(suite)
    val given = Given(suite)
    given.init()
    return given
  }

  inner class Given(val nestedSuite: TestSuite) {
    fun `when`(description: String, init: When.() -> Unit): When {
      val suite = TestSuite("When ${sanitizeSpecName(description)}")
      nestedSuite.addNestedSuite(suite)
      val `when` = When(suite)
      `when`.init()
      return `when`
    }
  }

  inner class When(val nestedSuite: TestSuite) {
    fun then(description: String, check: () -> Unit): TestCase {
      val testCase =
          TestCase(
              nestedSuite,
              "Then ${sanitizeSpecName(description)}",
              check,
              defaultTestCaseConfig)
      nestedSuite.addTestCase(testCase)
      check()
      return testCase
    }
  }
}
