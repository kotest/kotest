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
      addRootScope(TestContainer(rootDescription().append("Feature $name"), this@AbstractFeatureSpec::class, { FeatureScope(it).init() }))

  inner class FeatureScope(val context: TestContext) {

    fun and(name: String, init: FeatureScope.() -> Unit) =
        context.executeScope(TestContainer(context.currentScope().description().append("And $name"), this@AbstractFeatureSpec::class, { FeatureScope(it).init() }))

    fun scenario(name: String, test: TestContext.() -> Unit): TestCase {
      val tc = TestCase(context.currentScope().description().append("Scenario $name"), this@AbstractFeatureSpec, test, lineNumber(), defaultTestCaseConfig)
      context.executeScope(tc)
      return tc
    }
  }
}