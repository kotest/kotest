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
abstract class ShouldSpec(body: ShouldSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  operator fun String.invoke(init: ShouldScope.() -> Unit) =
      rootScope.addContainer(this, this@ShouldSpec, ::ShouldScope, init)

  inner class ShouldScope : TestScope() {

    operator fun String.invoke(init: ShouldScope.() -> Unit) =
        addContainer(this, this@ShouldSpec, ::ShouldScope, init)

    fun should(name: String, test: () -> Unit): TestCase =
        addTest(name, this@ShouldSpec, test, defaultTestCaseConfig)
  }

  fun should(name: String, test: () -> Unit): TestCase =
      rootScope.addTest(name, this@ShouldSpec, test, defaultTestCaseConfig)
}