package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

abstract class FeatureSpec(body: FeatureSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean {
    if (oneInstancePerTest)
      throw RuntimeException("This spec no longer supports using oneInstancePerTest. Only specs which do not use nested test scopes can use this feature")
    return false
  }

  fun feature(name: String, init: FeatureScope.() -> Unit) {
    val scope = TestScope("Feature: $name", this@FeatureSpec, { FeatureScope(TestScope.empty()).init() })
    rootScope.addScope(scope)
    FeatureScope(scope).init()
  }

  inner class FeatureScope(private val parentScope: TestScope) {
    fun scenario(name: String, test: () -> Unit): TestCase {
      val tc = TestCase("Scenario: $name", this@FeatureSpec, test, defaultTestCaseConfig)
      parentScope.addTest(tc)
      return tc
    }
  }
}