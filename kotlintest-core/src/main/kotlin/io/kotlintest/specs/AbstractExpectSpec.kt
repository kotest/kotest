package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

abstract class AbstractExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false


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
      context.registerTestScope(name, this@AbstractExpectSpec, test, config)
    }
  }

  inner class ExpectContext(val context: TestContext) {

    fun context(name: String, test: ExpectContext.() -> Unit) =
        context.registerTestScope("Context: $name", this@AbstractExpectSpec, { ExpectContext(this).test() }, defaultTestCaseConfig)

    fun expect(name: String, test: TestContext.() -> Unit) =
        context.registerTestScope("Expect: $name", this@AbstractExpectSpec, test, defaultTestCaseConfig)

    fun expect(name: String) = TestBuilder(context, "Expect: $name")
  }

  fun context(name: String, test: ExpectContext.() -> Unit) =
      addTestCase("Context: $name", { ExpectContext(this).test() }, defaultTestCaseConfig)


}