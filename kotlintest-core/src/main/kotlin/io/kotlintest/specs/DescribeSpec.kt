package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

abstract class DescribeSpec(body: DescribeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  fun describe(name: String, init: DescribeScope.() -> Unit) =
      rootScope.addContainer("Describe: $name", this@DescribeSpec, ::DescribeScope, init)

  inner class DescribeScope : TestScope() {

    fun describe(name: String, init: DescribeScope.() -> Unit) =
        addContainer("Describe: $name", this@DescribeSpec, ::DescribeScope, init)

    fun it(name: String, test: () -> Unit): TestCase =
        addTest(name, this@DescribeSpec, test, defaultTestCaseConfig)
  }
}