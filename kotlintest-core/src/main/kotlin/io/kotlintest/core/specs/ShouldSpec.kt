package io.kotlintest.core.specs

import io.kotlintest.core.AbstractSpec
import io.kotlintest.core.TestCase
import io.kotlintest.core.TestCaseDescriptor

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
    val descriptor = TestCaseDescriptor(this)
    specDescriptor.addDescriptor(descriptor)
    ShouldSpecScope(descriptor).init()
  }

  inner class ShouldSpecScope(private val parentDescriptor: TestCaseDescriptor) {

    operator fun String.invoke(init: ShouldSpecScope.() -> Unit) {
      val descriptor = TestCaseDescriptor(this)
      parentDescriptor.addDescriptor(descriptor)
      ShouldSpecScope(descriptor).init()
    }

    fun should(name: String, test: () -> Unit): TestCase = addTest(name, test, parentDescriptor)
  }

  private fun addTest(name: String, test: () -> Unit, parentDescriptor: TestCaseDescriptor): TestCase {
    val tc = TestCase("should $name", this@ShouldSpec, parentDescriptor, test, defaultTestCaseConfig)
    parentDescriptor.addTest(tc)
    return tc
  }

  fun should(name: String, test: () -> Unit): TestCase = addTest(name, test, specDescriptor)
}