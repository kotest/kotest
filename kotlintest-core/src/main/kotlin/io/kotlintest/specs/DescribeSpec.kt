package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

abstract class DescribeSpec(body: DescribeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun describe(name: String, init: DescribeScope.() -> Unit) {
    val descriptor = TestScope("Describe: $name", this@DescribeSpec, { DescribeScope(TestScope.empty()).init() })
    rootScope.addScope(descriptor)
    DescribeScope(descriptor).init()
  }

  inner class DescribeScope(private val parentScope: TestScope) {

    fun describe(name: String, init: DescribeScope.() -> Unit) {
      val scope = TestScope("Describe: $name", this@DescribeSpec, { DescribeScope(TestScope.empty()).init() })
      parentScope.addScope(scope)
      DescribeScope(scope).init()
    }

    fun it(name: String, test: () -> Unit): TestCase {
      val tc = TestCase(name, this@DescribeSpec, test, defaultTestCaseConfig)
      parentScope.addTest(tc)
      return tc
    }
  }
}