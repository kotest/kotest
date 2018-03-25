package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.TestContext
import io.kotlintest.lineNumber

abstract class AbstractFeatureSpec(body: AbstractFeatureSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  fun feature(name: String, init: FeatureScope.() -> Unit) =
      rootScopes.add(TestContainer("Feature $name", name() + "/" + "Feature $name", this@AbstractFeatureSpec, { FeatureScope(it).init() }))

  inner class FeatureScope(val context: TestContext) {

    fun and(name: String, init: FeatureScope.() -> Unit) =
        context.addScope(TestContainer("And $name", context.currentScope().path() + "/" + "And $name", this@AbstractFeatureSpec, { FeatureScope(it).init() }))

    fun scenario(name: String, test: TestContext.() -> Unit): TestCase {
      val tc = TestCase("Scenario $name", context.currentScope().path() + "/" + "Scenario $name", this@AbstractFeatureSpec, test, lineNumber(), defaultTestCaseConfig)
      context.addScope(tc)
      return tc
    }
  }
}