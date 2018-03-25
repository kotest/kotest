package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContext
import io.kotlintest.lineNumber

/**
 * Example:
 *
 * "my test" {
 * }
 *
 */
abstract class AbstractStringSpec(body: AbstractStringSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  // adds a test directly from the root context
  operator fun String.invoke(test: TestContext.() -> Unit): TestCase {
    val tc = TestCase(this, name() + "/" + this, this@AbstractStringSpec, test, lineNumber(), defaultTestCaseConfig)
    rootScopes.add(tc)
    return tc
  }
}