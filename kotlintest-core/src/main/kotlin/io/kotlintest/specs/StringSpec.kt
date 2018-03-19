package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer

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

  private fun addTest(desc: TestContainer, name: String, test: () -> Unit): TestCase {
    return TestCase(name, nextId(), this@StringSpec, desc, test, defaultTestCaseConfig)
  }

  // adds a test directly from the root context
  operator fun String.invoke(test: () -> Unit): TestCase = addTest(rootContainer, this, test)
}