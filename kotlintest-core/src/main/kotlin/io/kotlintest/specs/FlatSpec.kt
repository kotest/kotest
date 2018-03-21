package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

/**
 * Example:
 *
 * "some test" should "do something" `in` {
 *   // test here
 * }
 *
 */
abstract class FlatSpec(body: FlatSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  infix fun String.should(name: String): FlatScope {
    val scope = TestScope(this, this@FlatSpec, { })
    rootScope.addScope(scope)
    return FlatScope(scope, name)
  }

  inner class FlatScope(private val parentScope: TestScope, val name: String) {
    infix fun `in`(test: () -> Unit): TestCase {
      val tc = TestCase("should $name", this@FlatSpec, test, defaultTestCaseConfig)
      parentScope.addTest(tc)
      return tc
    }
  }
}

