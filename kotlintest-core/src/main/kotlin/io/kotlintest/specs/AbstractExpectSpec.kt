package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestContext

abstract class AbstractExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  fun context(name: String, test: TestContext.() -> Unit) =
      addTestCase("Context $name", test, defaultTestCaseConfig)

  fun expect(name: String, test: TestContext.() -> Unit) =
      addTestCase("Expect $name", test, defaultTestCaseConfig)
}