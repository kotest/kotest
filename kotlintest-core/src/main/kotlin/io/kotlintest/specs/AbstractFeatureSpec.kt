package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestType
import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

abstract class AbstractFeatureSpec(body: AbstractFeatureSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

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
      context.registerTestCase(name, this@AbstractFeatureSpec, test, config, TestType.Test)
    }
  }

  fun feature(name: String, init: FeatureScope.() -> Unit) =
      addTestCase("Feature: $name", { this@AbstractFeatureSpec.FeatureScope(this).init() }, defaultTestCaseConfig, TestType.Container)

  @KotlinTestDsl
  inner class FeatureScope(val context: TestContext) {

    fun and(name: String, init: FeatureScope.() -> Unit) =
        context.registerTestCase("And: $name", this@AbstractFeatureSpec, { this@AbstractFeatureSpec.FeatureScope(this).init() }, this@AbstractFeatureSpec.defaultTestCaseConfig, TestType.Container)

    fun scenario(name: String, test: TestContext.() -> Unit) =
        context.registerTestCase("Scenario: $name", this@AbstractFeatureSpec, test, this@AbstractFeatureSpec.defaultTestCaseConfig, TestType.Test)

    fun scenario(name: String) = this@AbstractFeatureSpec.ScenarioBuilder("Scenario: $name", context)
  }
}