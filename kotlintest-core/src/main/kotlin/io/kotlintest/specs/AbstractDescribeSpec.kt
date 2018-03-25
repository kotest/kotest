package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.TestContext
import io.kotlintest.lineNumber

abstract class AbstractDescribeSpec(body: AbstractDescribeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  fun describe(name: String, init: DescribeScope.() -> Unit) =
      rootScopes.add(TestContainer("Describe $name", name() + "/" + name, this@AbstractDescribeSpec, { DescribeScope(it).init() }))

  inner class DescribeScope(val context: TestContext) {

    fun describe(name: String, init: DescribeScope.() -> Unit) =
        context.addScope(TestContainer("Describe $name", name() + "/" + name, this@AbstractDescribeSpec, { DescribeScope(it).init() }))

    fun it(name: String, test: TestContext.() -> Unit): TestCase {
      val tc = TestCase("Scenario $name", context.currentScope().path() + "/" + name, this@AbstractDescribeSpec, test, lineNumber(), defaultTestCaseConfig)
      context.addScope(tc)
      return tc
    }
  }
}