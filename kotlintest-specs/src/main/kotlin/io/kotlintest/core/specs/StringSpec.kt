package io.kotlintest.core.specs

import io.kotlintest.core.AbstractSpec
import io.kotlintest.core.TestCase

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

  operator fun String.invoke(test: () -> Unit): TestCase {
    val tc = TestCase(this, this@StringSpec, specDescriptor, test, defaultTestCaseConfig)
    specDescriptor.addTest(tc)
    return tc
  }
}