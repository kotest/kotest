package io.kotest.specs

import io.kotest.Tag
import io.kotest.TestType
import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.core.specs.AbstractSpecDsl
import io.kotest.core.specs.KotestDsl
import io.kotest.core.specs.createTestName
import io.kotest.extensions.TestCaseExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Suppress("FunctionName")
abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : AbstractSpecDsl() {

  init {
    body()
  }

  fun Given(name: String, test: suspend GivenContext.() -> Unit) = addGivenContext(name, test)
  fun given(name: String, test: suspend GivenContext.() -> Unit) = addGivenContext(name, test)

  private fun addGivenContext(name: String, test: suspend GivenContext.() -> Unit) {
    addTestCase(createTestName("Given: ", name), { thisSpec.GivenContext(this).test() }, defaultTestCaseConfig, TestType.Container)
  }

  @KotestDsl
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

  @KotestDsl
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

  @KotestDsl
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

  @KotestDsl
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

  @KotestDsl
  inner class ThenContext(val context: TestContext)

   @KotestDsl
   inner class TestScope(val name: String, val context: TestContext) {
      @UseExperimental(ExperimentalTime::class)
      suspend fun config(
         invocations: Int? = null,
         enabled: Boolean? = null,
         timeout: Duration? = null,
         threads: Int? = null,
         tags: Set<Tag>? = null,
         extensions: List<TestCaseExtension>? = null,
         test: TestContext.() -> Unit) {
         val config = TestCaseConfig(
            enabled ?: this@BehaviorSpec.defaultTestCaseConfig.enabled,
            invocations ?: this@BehaviorSpec.defaultTestCaseConfig.invocations,
            timeout ?: this@BehaviorSpec.defaultTestCaseConfig.timeout,
            threads ?: this@BehaviorSpec.defaultTestCaseConfig.threads,
            tags ?: this@BehaviorSpec.defaultTestCaseConfig.tags,
            extensions ?: this@BehaviorSpec.defaultTestCaseConfig.extensions)

         context.registerTestCase(name, this@BehaviorSpec, { test.invoke(this) }, config, TestType.Test)
      }
   }

  private val thisSpec: BehaviorSpec
    get() = this@BehaviorSpec

}
