package io.kotlintest.core.specs

import io.kotlintest.core.AbstractSpec
import io.kotlintest.core.TestCase
import io.kotlintest.core.TestCaseDescriptor

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

  private fun addTest(desc: TestCaseDescriptor, name: String, test: () -> Unit): TestCase {
    return TestCase(name, this@StringSpec, desc, test, defaultTestCaseConfig)
  }

  // adds a test directly from the root context
  operator fun String.invoke(test: () -> Unit): TestCase = addTest(specDescriptor, this, test)
}