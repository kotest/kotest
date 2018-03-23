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
abstract class AbstractWordSpec(body: AbstractWordSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  infix fun String.should(init: WordSpecScope.() -> Unit) =
      rootScope.addContainer(this, this@AbstractWordSpec, ::WordSpecScope, init)

  inner class WordSpecScope : TestScope() {
    infix operator fun String.invoke(test: () -> Unit): TestCase =
        addTest("should " + this, this@AbstractWordSpec, test, defaultTestCaseConfig)
  }
}