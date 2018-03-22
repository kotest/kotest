package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase

/**
 * Example:
 *
 * "my test" {
 * }
 *
 */
abstract class StringSpec(body: StringSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  // adds a test directly from the root context
  operator fun String.invoke(test: () -> Unit): TestCase =
      rootScope.addTest(this, this@StringSpec, test, defaultTestCaseConfig)
}