package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

abstract class AbstractFeatureSpec(body: AbstractFeatureSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  inner class ScenarioBuilder(val name: String, val context: TestContext) {
    fun config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: TestContext.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: defaultTestCaseConfig.enabled,
          invocations ?: defaultTestCaseConfig.invocations,
          timeout ?: defaultTestCaseConfig.timeout,
          threads ?: defaultTestCaseConfig.threads,
          tags ?: defaultTestCaseConfig.tags,
          extensions ?: defaultTestCaseConfig.extensions)
      context.registerTestScope(name, this@AbstractFeatureSpec, test, config)
    }
  }

  fun feature(name: String, init: FeatureContext.() -> Unit) =
      addTestCase("Feature $name", { FeatureContext(this).init() }, defaultTestCaseConfig)

  inner class FeatureContext(val context: TestContext) {

    fun and(name: String, init: FeatureContext.() -> Unit) =
        addTestCase("And $name", { FeatureContext(this).init() }, defaultTestCaseConfig)

    fun scenario(name: String, test: TestContext.() -> Unit) =
        addTestCase("Scenario $name", test, defaultTestCaseConfig)

    fun scenario(name: String) = ScenarioBuilder("Scenario $name", context)
  }
}