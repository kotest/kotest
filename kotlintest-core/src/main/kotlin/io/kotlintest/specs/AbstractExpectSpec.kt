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
    rootScopes.add(TestContainer("Context $name", this@AbstractExpectSpec, { ExpectContext(it).init() }))
  }

  inner class ExpectContext(val context: TestContext) {

    fun context(name: String, init: ExpectContext.() -> Unit) =
        context.addScope(TestContainer("Context $name", this@AbstractExpectSpec, { ExpectContext(it).init() }))

    fun expect(name: String, test: TestContext.() -> Unit): TestCase {
      val tc = TestCase("Expect $name", this@AbstractExpectSpec, test, lineNumber(), defaultTestCaseConfig)
      context.addScope(tc)
      return tc
    }
  }
}