package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestType
import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

@Suppress("FunctionName")
abstract class AbstractBehaviorSpec(body: AbstractBehaviorSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  @KotlinTestDsl
  inner class GivenContext(val context: TestContext) {
    suspend fun And(name: String, test: suspend WhenContext.() -> Unit) = and(name, test)
    suspend fun and(name: String, test: suspend WhenContext.() -> Unit) = add("And: $name", test)
    suspend fun When(name: String, test: suspend WhenContext.() -> Unit) = `when`(name, test)
    suspend fun `when`(name: String, test: suspend WhenContext.() -> Unit) = add("When: $name", test)
    private suspend fun add(name: String, test: suspend WhenContext.() -> Unit) =
        context.registerTestCase(name, this@AbstractBehaviorSpec, { this@AbstractBehaviorSpec.WhenContext(this).test() }, this@AbstractBehaviorSpec.defaultTestCaseConfig, TestType.Container)
  }

  @KotlinTestDsl
  inner class WhenContext(val context: TestContext) {
    suspend fun Then(name: String, test: suspend TestContext.() -> Unit) = then(name, test)
    suspend fun then(name: String, test: suspend TestContext.() -> Unit) =
        context.registerTestCase("Then: $name", this@AbstractBehaviorSpec, test, this@AbstractBehaviorSpec.defaultTestCaseConfig, TestType.Test)

    fun then(name: String): ThenScope = ThenScope(name, context)
  }

  @KotlinTestDsl
  inner class ThenScope(val name: String, val context: TestContext) {

    suspend fun config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: TestContext.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: this@AbstractBehaviorSpec.defaultTestCaseConfig.enabled,
          invocations ?: this@AbstractBehaviorSpec.defaultTestCaseConfig.invocations,
          timeout ?: this@AbstractBehaviorSpec.defaultTestCaseConfig.timeout,
          threads ?: this@AbstractBehaviorSpec.defaultTestCaseConfig.threads,
          tags ?: this@AbstractBehaviorSpec.defaultTestCaseConfig.tags,
          extensions ?: this@AbstractBehaviorSpec.defaultTestCaseConfig.extensions)
      context.registerTestCase(name, this@AbstractBehaviorSpec, { test.invoke(this) }, config, TestType.Test)
    }
  }

  fun Given(name: String, test: suspend GivenContext.() -> Unit) = given(name, test)
  fun given(name: String, test: suspend GivenContext.() -> Unit) =
      addTestCase("Given: $name", { this@AbstractBehaviorSpec.GivenContext(this).test() }, defaultTestCaseConfig, TestType.Container)
}
