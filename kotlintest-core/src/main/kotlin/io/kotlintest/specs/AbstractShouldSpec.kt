package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

/**
 * Example:
 *
 * "some test" {
 *   "with context" {
 *      should("do something") {
 *        // test here
 *      }
 *    }
 *  }
 *
 *  or
 *
 *  should("do something") {
 *    // test here
 *  }
 */
abstract class AbstractShouldSpec(body: AbstractShouldSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  operator fun String.invoke(init: ShouldScope.() -> Unit) =
      rootScope.addContainer(this, this@AbstractShouldSpec, ::ShouldScope, init)

  inner class ShouldScope : TestScope() {

    operator fun String.invoke(init: ShouldScope.() -> Unit) =
        addContainer(this, this@AbstractShouldSpec, ::ShouldScope, init)

    fun should(name: String, test: () -> Unit): TestCase =
        addTest(name, this@AbstractShouldSpec, test, defaultTestCaseConfig)
  }

  fun should(name: String, test: () -> Unit): TestCase =
      rootScope.addTest(name, this@AbstractShouldSpec, test, defaultTestCaseConfig)
}