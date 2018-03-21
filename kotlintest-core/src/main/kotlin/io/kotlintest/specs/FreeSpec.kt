package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

abstract class FreeSpec(body: FreeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  infix operator fun String.minus(init: FreeSpecScope.() -> Unit) {
    val scope = TestScope(this, this@FreeSpec, { FreeSpecScope(TestScope.empty()).init() })
    rootScope.addScope(scope)
    FreeSpecScope(scope).init()
  }

  inner class FreeSpecScope(private val parentScope: TestScope) {

    infix operator fun String.minus(init: FreeSpecScope.() -> Unit) {
      val scope = TestScope(this, this@FreeSpec, { FreeSpecScope(TestScope.empty()).init() })
      parentScope.addScope(scope)
      FreeSpecScope(scope).init()
    }

    infix operator fun String.invoke(test: () -> Unit): TestCase {
      val testcase = TestCase(this, this@FreeSpec, test, defaultTestCaseConfig)
      parentScope.addTest(testcase)
      return testcase
    }
  }
}