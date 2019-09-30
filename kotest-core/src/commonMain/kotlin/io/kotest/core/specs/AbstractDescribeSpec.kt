package io.kotest.core.specs

import io.kotest.Tag
import io.kotest.TestType
import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.extensions.TestCaseExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

abstract class AbstractDescribeSpec(body: AbstractDescribeSpec.() -> Unit = {}) : AbstractSpecDsl() {

  init {
    body()
  }

  @KotestDsl
  inner class TestBuilder(val context: TestContext, val name: String) {

    @UseExperimental(ExperimentalTime::class)
    suspend fun config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        parallelism: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: suspend TestContext.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: this@AbstractDescribeSpec.defaultTestCaseConfig.enabled,
          invocations ?: this@AbstractDescribeSpec.defaultTestCaseConfig.invocations,
          timeout ?: this@AbstractDescribeSpec.defaultTestCaseConfig.timeout,
          parallelism ?: this@AbstractDescribeSpec.defaultTestCaseConfig.threads,
          tags ?: this@AbstractDescribeSpec.defaultTestCaseConfig.tags,
          extensions ?: this@AbstractDescribeSpec.defaultTestCaseConfig.extensions)
      context.registerTestCase(name, this@AbstractDescribeSpec, test, config, TestType.Test)
    }
  }

  @KotestDsl
  inner class DescribeScope(val context: TestContext) {

    fun it(name: String) = this@AbstractDescribeSpec.TestBuilder(context, "It: $name")
    suspend fun it(name: String, test: suspend TestContext.() -> Unit) =
        context.registerTestCase(createTestName("It: ", name), this@AbstractDescribeSpec, test, this@AbstractDescribeSpec.defaultTestCaseConfig, TestType.Test)

    suspend fun context(name: String, test: suspend DescribeScope.() -> Unit) =
        context.registerTestCase(createTestName("Context: ", name), this@AbstractDescribeSpec, { this@AbstractDescribeSpec.DescribeScope(this).test() }, this@AbstractDescribeSpec.defaultTestCaseConfig, TestType.Container)

    suspend fun describe(name: String, test: suspend DescribeScope.() -> Unit) =
            context.registerTestCase(createTestName("Describe: ", name), this@AbstractDescribeSpec, { this@AbstractDescribeSpec.DescribeScope(this).test() }, this@AbstractDescribeSpec.defaultTestCaseConfig, TestType.Container)
  }

  fun describe(name: String, test: suspend DescribeScope.() -> Unit) =
      addTestCase(createTestName("Describe: ", name), { this@AbstractDescribeSpec.DescribeScope(this).test() }, defaultTestCaseConfig, TestType.Container)

}
