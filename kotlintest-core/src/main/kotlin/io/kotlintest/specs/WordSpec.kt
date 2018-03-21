package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

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
    val descriptor = TestScope(this, this@WordSpec)
    rootContainer.addScope(descriptor)
    ShouldScope(descriptor).init()
  }

  inner class ShouldScope(private val parentDescriptor: TestScope) {
    infix operator fun String.invoke(test: () -> Unit): TestCase {
      val tc = TestCase("should " + this, this@WordSpec, test, defaultTestCaseConfig)
      parentDescriptor.addTest(tc)
      return tc
    }
  }
}

