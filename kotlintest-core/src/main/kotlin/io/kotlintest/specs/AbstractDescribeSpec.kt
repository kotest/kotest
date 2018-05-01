package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

abstract class AbstractDescribeSpec(body: AbstractDescribeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

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
          enabled ?: defaultTestCaseConfig.enabled,
          invocations ?: defaultTestCaseConfig.invocations,
          timeout ?: defaultTestCaseConfig.timeout,
          threads ?: defaultTestCaseConfig.threads,
          tags ?: defaultTestCaseConfig.tags,
          extensions ?: defaultTestCaseConfig.extensions)
      context.registerTestScope(name, this@AbstractDescribeSpec, test, config)
    }
  }

  inner class DescribeContext(val context: TestContext) {

    fun it(name: String) = TestBuilder(context, "Scenario: $name")
    fun it(name: String, test: TestContext.() -> Unit) =
        context.registerTestScope("Scenario: $name", this@AbstractDescribeSpec, test, defaultTestCaseConfig)

    fun context(name: String, test: DescribeContext.() -> Unit) =
        context.registerTestScope("Context: $name", this@AbstractDescribeSpec, { DescribeContext(this).test() }, defaultTestCaseConfig)
  }

  fun describe(name: String, test: DescribeContext.() -> Unit) =
      addTestCase("Describe: $name", { DescribeContext(this).test() }, defaultTestCaseConfig)

}