package io.kotest.core.specs

import io.kotest.Tag
import io.kotest.TestType
import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.extensions.TestCaseExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

abstract class AbstractExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractSpecDsl() {

  init {
    body()
  }

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
            enabled ?: defaultTestCaseConfig.enabled,
            invocations ?: defaultTestCaseConfig.invocations,
            timeout ?: defaultTestCaseConfig.timeout,
            parallelism ?: defaultTestCaseConfig.threads,
            tags ?: defaultTestCaseConfig.tags,
            extensions ?: defaultTestCaseConfig.extensions)
         context.registerTestCase(name, this@AbstractExpectSpec, test, config, TestType.Test)
      }
   }

  @KotestDsl
  inner class ExpectScope(val context: TestContext) {

    suspend fun context(name: String, test: suspend ExpectScope.() -> Unit) =
        context.registerTestCase(createTestName("Context: ", name), this@AbstractExpectSpec, { this@AbstractExpectSpec.ExpectScope(this).test() }, this@AbstractExpectSpec.defaultTestCaseConfig, TestType.Container)

    suspend fun expect(name: String, test: suspend TestContext.() -> Unit) =
        context.registerTestCase(createTestName("Expect: ", name), this@AbstractExpectSpec, test, this@AbstractExpectSpec.defaultTestCaseConfig, TestType.Test)

    fun expect(name: String) = this@AbstractExpectSpec.TestBuilder(context, createTestName("Expect: ", name))
  }

  fun context(name: String, test: suspend ExpectScope.() -> Unit) =
      addTestCase(createTestName("Context: ", name), { this@AbstractExpectSpec.ExpectScope(this).test() }, defaultTestCaseConfig, TestType.Container)


}
