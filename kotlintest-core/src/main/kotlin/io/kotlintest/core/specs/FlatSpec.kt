package io.kotlintest.core.specs

import io.kotlintest.core.AbstractSpec
import io.kotlintest.core.TestCase
import io.kotlintest.core.TestCaseDescriptor

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
    val descriptor = TestCaseDescriptor(this)
    specDescriptor.addDescriptor(descriptor)
    return FlatScope(descriptor, name)
  }

  inner class FlatScope(private val parentDescriptor: TestCaseDescriptor, val name: String) {
    infix fun `in`(test: () -> Unit): TestCase {
      val tc = TestCase("should $name", this@FlatSpec, parentDescriptor, test, defaultTestCaseConfig)
      parentDescriptor.addTest(tc)
      return tc
    }
  }
}

