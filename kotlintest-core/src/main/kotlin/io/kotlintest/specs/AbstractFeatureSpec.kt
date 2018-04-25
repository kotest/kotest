package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestContainer
import io.kotlintest.TestContext

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

    fun scenario(name: String) = TestBuilder(context, "Scenario $name")
    fun scenario(name: String, test: TestContext.() -> Unit) = scenario(name).invoke(test)
  }
}