package io.kotlintest.core.specs

import io.kotlintest.Tag
import io.kotlintest.TestType
import io.kotlintest.core.TestCaseConfig
import io.kotlintest.core.TestContext
import io.kotlintest.extensions.TestCaseExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

abstract class AbstractFunSpec(body: AbstractFunSpec.() -> Unit = {}) : AbstractSpecDsl() {

   init {
      body()
   }

   inner class TestBuilder(val name: String) {
      @UseExperimental(ExperimentalTime::class)
      fun config(
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
         addTestCase(name, test, config, TestType.Test)
      }
   }

   fun context(name: String, init: suspend ContextScope.() -> Unit) {
      addTestCase(name, { ContextScope(this).init() }, defaultTestCaseConfig, TestType.Container)
   }

   @KotlinTestDsl
   inner class ContextScope(val context: TestContext) {

      suspend fun context(name: String, init: suspend ContextScope.() -> Unit) {
         context.registerTestCase(
            name,
            this@AbstractFunSpec,
            { ContextScope(this).init() },
            defaultTestCaseConfig,
            TestType.Container
         )
      }

      inner class TestBuilder(val name: String) {
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
            context.registerTestCase(name, this@AbstractFunSpec, test, config, TestType.Test)
         }
      }

      fun test(name: String) = TestBuilder(name)

      suspend fun test(name: String, test: suspend TestContext.() -> Unit) =
         context.registerTestCase(name, this@AbstractFunSpec, test, defaultTestCaseConfig, TestType.Test)
   }

   fun test(name: String) = TestBuilder(name)

   fun test(name: String, test: suspend TestContext.() -> Unit) =
      addTestCase(name, test, defaultTestCaseConfig, TestType.Test)
}
