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

  fun Given(name: String, test: suspend GivenContext.() -> Unit) = addGivenContext(name, test)
  fun given(name: String, test: suspend GivenContext.() -> Unit) = addGivenContext(name, test)

  private fun addGivenContext(name: String, test: suspend GivenContext.() -> Unit) {
    addTestCase(createTestName("Given: ", name), { thisSpec.GivenContext(this).test() }, defaultTestCaseConfig, TestType.Container)
  }

  @KotlinTestDsl
  inner class GivenContext(val context: TestContext) {
    suspend fun And(name: String, test: suspend GivenAndContext.() -> Unit) = addAndContext(name, test)
    suspend fun and(name: String, test: suspend GivenAndContext.() -> Unit) = addAndContext(name, test)

    private suspend fun addAndContext(name: String, test: suspend GivenAndContext.() -> Unit) {
      context.registerTestCase(createTestName("And: ", name), thisSpec, { thisSpec.GivenAndContext(this).test() }, thisSpec.defaultTestCaseConfig, TestType.Container)
    }

    suspend fun When(name: String, test: suspend WhenContext.() -> Unit) = addWhenContext(name, test)
    suspend fun `when`(name: String, test: suspend WhenContext.() -> Unit) = addWhenContext(name, test)

    private suspend fun addWhenContext(name: String, test: suspend WhenContext.() -> Unit) {
      context.registerTestCase(createTestName("When: ", name), thisSpec, { thisSpec.WhenContext(this).test() }, thisSpec.defaultTestCaseConfig, TestType.Container)
    }

    suspend fun Then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)
    suspend fun then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)

    private suspend fun addThenContext(name: String, test: suspend ThenContext.() -> Unit) {
      context.registerTestCase(createTestName("Then: ", name), thisSpec, { thisSpec.ThenContext(this).test() }, thisSpec.defaultTestCaseConfig, TestType.Container)
    }

    fun then(name: String) = TestScope(name, context)
  }

  @KotlinTestDsl
  inner class GivenAndContext(val context: TestContext) {
    suspend fun And(name: String, test: suspend GivenAndContext.() -> Unit) = addAndContext(name, test)
    suspend fun and(name: String, test: suspend GivenAndContext.() -> Unit) = addAndContext(name, test)

    private suspend fun addAndContext(name: String, test: suspend GivenAndContext.() -> Unit) {
      context.registerTestCase(createTestName("And: ", name), thisSpec, { thisSpec.GivenAndContext(this).test() }, thisSpec.defaultTestCaseConfig, TestType.Container)
    }

    suspend fun When(name: String, test: suspend WhenContext.() -> Unit) = addWhenContext(name, test)
    suspend fun `when`(name: String, test: suspend WhenContext.() -> Unit) = addWhenContext(name, test)

    private suspend fun addWhenContext(name: String, test: suspend WhenContext.() -> Unit) {
      context.registerTestCase(createTestName("When: ", name), thisSpec, { thisSpec.WhenContext(this).test() }, thisSpec.defaultTestCaseConfig, TestType.Container)
    }

    suspend fun Then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)
    suspend fun then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)

    private suspend fun addThenContext(name: String, test: suspend ThenContext.() -> Unit) {
      context.registerTestCase(createTestName("Then: ", name), thisSpec, { thisSpec.ThenContext(this).test() }, thisSpec.defaultTestCaseConfig, TestType.Container)
    }

    fun then(name: String) = TestScope(name, context)
  }

  @KotlinTestDsl
  inner class WhenContext(val context: TestContext) {
    suspend fun And(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)
    suspend fun and(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)

    private suspend fun addAndContext(name: String, test: suspend WhenAndContext.() -> Unit) {
      context.registerTestCase(createTestName("And: ", name), thisSpec, { thisSpec.WhenAndContext(this).test() }, thisSpec.defaultTestCaseConfig, TestType.Container)
    }

    suspend fun Then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)
    suspend fun then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)

    private suspend fun addThenContext(name: String, test: suspend ThenContext.() -> Unit) {
      context.registerTestCase(createTestName("Then: ", name), thisSpec, { thisSpec.ThenContext(this).test() }, thisSpec.defaultTestCaseConfig, TestType.Test)
    }

    fun then(name: String) = TestScope(name, context)
  }

  @KotlinTestDsl
  inner class WhenAndContext(val context: TestContext) {
    suspend fun And(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)
    suspend fun and(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)

    private suspend fun addAndContext(name: String, test: suspend WhenAndContext.() -> Unit) {
      context.registerTestCase(createTestName("And: ", name), thisSpec, { thisSpec.WhenAndContext(this).test() }, thisSpec.defaultTestCaseConfig, TestType.Container)
    }

    suspend fun Then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)
    suspend fun then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)

    private suspend fun addThenContext(name: String, test: suspend ThenContext.() -> Unit) {
      context.registerTestCase(createTestName("Then: ", name), thisSpec, { thisSpec.ThenContext(this).test() }, thisSpec.defaultTestCaseConfig, TestType.Test)
    }

    fun then(name: String) = TestScope(name, context)
  }

  @KotlinTestDsl
  inner class ThenContext(val context: TestContext)

  @KotlinTestDsl
  inner class TestScope(val name: String, val context: TestContext) {
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

  private val thisSpec: AbstractBehaviorSpec
    get() = this@AbstractBehaviorSpec

}
