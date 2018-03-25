package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.TestContext
import io.kotlintest.lineNumber

/**
 * Example:
 *
 * "some test" should {
 *    "do something" {
 *      // test here
 *    }
 * }
 *
 */
abstract class AbstractWordSpec(body: AbstractWordSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  infix fun String.should(init: WordContext.() -> Unit) =
      rootScopes.add(TestContainer(this, name() + "/" + this, this@AbstractWordSpec, { WordContext(it).init() }))

  inner class WordContext(val context: TestContext) {
    infix operator fun String.invoke(test: TestContext.() -> Unit): TestCase {
      val tc = TestCase("should " + this, context.currentScope().path() + "/" + "should " + this, this@AbstractWordSpec, test, lineNumber(), defaultTestCaseConfig)
      context.addScope(tc)
      return tc
    }
  }
}