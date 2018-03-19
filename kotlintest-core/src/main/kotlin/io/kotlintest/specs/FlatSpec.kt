package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer

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
    val descriptor = TestContainer(this, this@FlatSpec)
    rootContainer.addContainer(descriptor)
    return FlatScope(descriptor, name)
  }

  inner class FlatScope(private val parentDescriptor: TestContainer, val name: String) {
    infix fun `in`(test: () -> Unit): TestCase {
      val tc = TestCase("should $name", this@FlatSpec, test, defaultTestCaseConfig)
      parentDescriptor.addTest(tc)
      return tc
    }
  }
}

