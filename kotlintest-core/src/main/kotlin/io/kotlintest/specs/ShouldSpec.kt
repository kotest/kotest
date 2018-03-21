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

  operator fun String.invoke(init: ShouldSpecScope.() -> Unit) {
    val descriptor = TestScope(this, this@ShouldSpec)
    rootContainer.addScope(descriptor)
    ShouldSpecScope(descriptor).init()
  }

  inner class ShouldSpecScope(private val parentDescriptor: TestScope) {

    operator fun String.invoke(init: ShouldSpecScope.() -> Unit) {
      val descriptor = TestScope(this, this@ShouldSpec)
      parentDescriptor.addScope(descriptor)
      ShouldSpecScope(descriptor).init()
    }

    fun should(name: String, test: () -> Unit): TestCase = addTest(name, test, parentDescriptor)
  }

  private fun addTest(name: String, test: () -> Unit, parentDescriptor: TestScope): TestCase {
    val tc = TestCase("should $name", this@ShouldSpec, test, defaultTestCaseConfig)
    parentDescriptor.addTest(tc)
    return tc
  }

  fun should(name: String, test: () -> Unit): TestCase = addTest(name, test, rootContainer)
}