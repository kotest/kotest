package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestType
import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

abstract class AbstractExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractSpec() {

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
        test: suspend TestContext.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: defaultTestCaseConfig.enabled,
          invocations ?: defaultTestCaseConfig.invocations,
          timeout ?: defaultTestCaseConfig.timeout,
          threads ?: defaultTestCaseConfig.threads,
          tags ?: defaultTestCaseConfig.tags,
          extensions ?: defaultTestCaseConfig.extensions)
      context.registerTestCase(name, this@AbstractExpectSpec, test, config, TestType.Test)
    }
  }

  @KotlinTestDsl
  inner class ExpectScope(val context: TestContext) {

    fun expect(name: String, test: suspend TestContext.() -> Unit) =
        context.registerTestCase("Expect: $name", this@AbstractExpectSpec, test, this@AbstractExpectSpec.defaultTestCaseConfig, TestType.Test)

    fun expect(name: String) = this@AbstractExpectSpec.TestBuilder(context, "Expect: $name")
  }

  fun context(name: String, test: ExpectScope.() -> Unit) =
      addTestCase("Context: $name", { this@AbstractExpectSpec.ExpectScope(this).test() }, defaultTestCaseConfig, TestType.Container)


}