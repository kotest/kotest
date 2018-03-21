package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

/**
 * Example:
 *
 * "some test" should {
 *    "do something" {
 *      // test here
 *    }
 * }
 *
 */
abstract class WordSpec(body: WordSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  infix fun String.should(init: ShouldScope.() -> Unit) {
    val scope = TestScope(this, this@WordSpec, { ShouldScope(TestScope.empty()).init() })
    rootScope.addScope(scope)
    ShouldScope(scope).init()
  }

  inner class ShouldScope(private val parentScope: TestScope) {
    infix operator fun String.invoke(test: () -> Unit): TestCase {
      val tc = TestCase("should " + this, this@WordSpec, test, defaultTestCaseConfig)
      parentScope.addTest(tc)
      return tc
    }
  }
}

