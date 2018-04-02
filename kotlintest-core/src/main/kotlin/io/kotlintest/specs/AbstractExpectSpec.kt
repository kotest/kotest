package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.TestContext
import io.kotlintest.lineNumber

abstract class AbstractExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  fun context(name: String, init: ExpectContext.() -> Unit) {
    addRootScope(TestContainer(rootDescription().append("Context $name"), this@AbstractExpectSpec, { ExpectContext(it).init() }))
  }

  inner class ExpectContext(val context: TestContext) {

    fun context(name: String, init: ExpectContext.() -> Unit) =
        context.addScope(TestContainer(context.currentScope().description().append("Context $name"), this@AbstractExpectSpec, { ExpectContext(it).init() }))

    fun expect(name: String, test: TestContext.() -> Unit): TestCase {
      val tc = TestCase(context.currentScope().description().append("Expect $name"), this@AbstractExpectSpec, test, lineNumber(), defaultTestCaseConfig)
      context.addScope(tc)
      return tc
    }
  }
}