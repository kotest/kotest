package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.TestContext
import io.kotlintest.lineNumber

abstract class AbstractFreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  infix operator fun String.minus(init: FreeSpecContext.() -> Unit) =
      rootScopes.add(TestContainer(this, name() + "/" + this, this@AbstractFreeSpec, { FreeSpecContext(it).init() }))

  inner class FreeSpecContext(val context: TestContext) {

    infix operator fun String.minus(init: FreeSpecContext.() -> Unit) =
        context.addScope(TestContainer(this, context.currentScope().path() + "/" + this, this@AbstractFreeSpec, { FreeSpecContext(it).init() }))

    infix operator fun String.invoke(test: TestContext.() -> Unit): TestCase {
      val tc = TestCase(this, context.currentScope().path() + "/" + this, this@AbstractFreeSpec, test, lineNumber(), defaultTestCaseConfig)
      context.addScope(tc)
      return tc
    }
  }
}