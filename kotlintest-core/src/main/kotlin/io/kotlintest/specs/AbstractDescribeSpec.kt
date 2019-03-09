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

  @KotlinTestDsl
  inner class DescribeScope(val context: TestContext) {

    fun it(name: String) = this@AbstractDescribeSpec.TestBuilder(context, "It: $name")
    suspend fun it(name: String, test: suspend TestContext.() -> Unit) =
        context.registerTestCase(createTestName("It: ", name), this@AbstractDescribeSpec, test, this@AbstractDescribeSpec.defaultTestCaseConfig, TestType.Test)

    suspend fun context(name: String, test: suspend DescribeScope.() -> Unit) =
        context.registerTestCase(createTestName("Context: ", name), this@AbstractDescribeSpec, { this@AbstractDescribeSpec.DescribeScope(this).test() }, this@AbstractDescribeSpec.defaultTestCaseConfig, TestType.Container)
  }

  fun describe(name: String, test: suspend DescribeScope.() -> Unit) =
      addTestCase(createTestName("Describe: ", name), { this@AbstractDescribeSpec.DescribeScope(this).test() }, defaultTestCaseConfig, TestType.Container)

}