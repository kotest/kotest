package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestType
import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

abstract class AbstractDescribeSpec(body: AbstractDescribeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  @KotlinTestDsl
  inner class TestBuilder(val context: TestContext, val name: String) {

    fun config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: TestContext.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: this@AbstractDescribeSpec.defaultTestCaseConfig.enabled,
          invocations ?: this@AbstractDescribeSpec.defaultTestCaseConfig.invocations,
          timeout ?: this@AbstractDescribeSpec.defaultTestCaseConfig.timeout,
          threads ?: this@AbstractDescribeSpec.defaultTestCaseConfig.threads,
          tags ?: this@AbstractDescribeSpec.defaultTestCaseConfig.tags,
          extensions ?: this@AbstractDescribeSpec.defaultTestCaseConfig.extensions)
      context.registerTestCase(name, this@AbstractDescribeSpec, test, config, TestType.Test)
    }
  }

  @KotlinTestDsl
  inner class DescribeScope(val context: TestContext) {

    fun it(name: String) = this@AbstractDescribeSpec.TestBuilder(context, "Scenario: $name")
    fun it(name: String, test: TestContext.() -> Unit) =
        context.registerTestCase("Scenario: $name", this@AbstractDescribeSpec, test, this@AbstractDescribeSpec.defaultTestCaseConfig, TestType.Test)

    fun context(name: String, test: DescribeScope.() -> Unit) =
        context.registerTestCase("Context: $name", this@AbstractDescribeSpec, { this@AbstractDescribeSpec.DescribeScope(this).test() }, this@AbstractDescribeSpec.defaultTestCaseConfig, TestType.Container)
  }

  fun describe(name: String, test: DescribeScope.() -> Unit) =
      addTestCase("Describe: $name", { this@AbstractDescribeSpec.DescribeScope(this).test() }, defaultTestCaseConfig, TestType.Container)

}