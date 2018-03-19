package io.kotlintest.core.specs

import io.kotlintest.core.AbstractSpec
import io.kotlintest.core.TestCase
import io.kotlintest.core.TestCaseDescriptor

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
abstract class WordSpec(body: WordSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  infix fun String.should(init: ShouldScope.() -> Unit) {
    val descriptor = TestCaseDescriptor(this)
    specDescriptor.addDescriptor(descriptor)
    ShouldScope(descriptor).init()
  }

  inner class ShouldScope(private val parentDescriptor: TestCaseDescriptor) {
    infix operator fun String.invoke(test: () -> Unit): TestCase {
      val tc = TestCase("should " + this, this@WordSpec, parentDescriptor, test, defaultTestCaseConfig)
      parentDescriptor.addTest(tc)
      return tc
    }
  }
}

