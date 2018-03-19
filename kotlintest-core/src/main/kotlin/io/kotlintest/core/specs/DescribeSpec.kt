package io.kotlintest.core.specs

import io.kotlintest.core.AbstractSpec
import io.kotlintest.core.TestCase
import io.kotlintest.core.TestCaseDescriptor

abstract class DescribeSpec(body: DescribeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun describe(name: String, init: DescribeScope.() -> Unit) {
    val descriptor = TestCaseDescriptor("Describe: $name")
    specDescriptor.addDescriptor(descriptor)
    DescribeScope(descriptor).init()
  }

  inner class DescribeScope(private val parentDescriptor: TestCaseDescriptor) {

    fun describe(name: String, init: DescribeScope.() -> Unit) {
      val descriptor = TestCaseDescriptor("Describe: $name")
      parentDescriptor.addDescriptor(descriptor)
      DescribeScope(descriptor).init()
    }

    fun it(name: String, test: () -> Unit): TestCase {
      val tc = TestCase(name, this@DescribeSpec, parentDescriptor, test, defaultTestCaseConfig)
      parentDescriptor.addTest(tc)
      return tc
    }
  }
}