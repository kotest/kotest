package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer

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
    val descriptor = TestContainer(this)
    rootContainer.addContainer(descriptor)
    ShouldScope(descriptor).init()
  }

  inner class ShouldScope(private val parentDescriptor: TestContainer) {
    infix operator fun String.invoke(test: () -> Unit): TestCase {
      val tc = TestCase("should " + this, nextId(), this@WordSpec, parentDescriptor, test, defaultTestCaseConfig)
      parentDescriptor.addTest(tc)
      return tc
    }
  }
}

