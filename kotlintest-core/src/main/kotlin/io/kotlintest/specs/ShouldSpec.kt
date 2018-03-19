package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer

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
    val descriptor = TestContainer(this, this@ShouldSpec)
    rootContainer.addContainer(descriptor)
    ShouldSpecScope(descriptor).init()
  }

  inner class ShouldSpecScope(private val parentDescriptor: TestContainer) {

    operator fun String.invoke(init: ShouldSpecScope.() -> Unit) {
      val descriptor = TestContainer(this, this@ShouldSpec)
      parentDescriptor.addContainer(descriptor)
      ShouldSpecScope(descriptor).init()
    }

    fun should(name: String, test: () -> Unit): TestCase = addTest(name, test, parentDescriptor)
  }

  private fun addTest(name: String, test: () -> Unit, parentDescriptor: TestContainer): TestCase {
    val tc = TestCase("should $name", this@ShouldSpec, test, defaultTestCaseConfig)
    parentDescriptor.addTest(tc)
    return tc
  }

  fun should(name: String, test: () -> Unit): TestCase = addTest(name, test, rootContainer)
}