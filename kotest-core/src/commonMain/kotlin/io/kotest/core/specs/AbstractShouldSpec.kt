package io.kotest.core.specs

import io.kotest.Tag
import io.kotest.TestType
import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.extensions.TestCaseExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Example:
 *
 * "some test" {
 *   "with context" {
 *      should("do something") {
 *        // test here
 *      }
 *    }
 *  }
 *
 *  or
 *
 *  should("do something") {
 *    // test here
 *  }
 */
abstract class AbstractShouldSpec(body: AbstractShouldSpec.() -> Unit = {}) : AbstractSpecDsl() {

  init {
    body()
  }

  operator fun String.invoke(init: suspend ShouldScope.() -> Unit) =
      addTestCase(this, { this@AbstractShouldSpec.ShouldScope(this).init() }, defaultTestCaseConfig, TestType.Container)

  fun should(name: String, test: suspend TestContext.() -> Unit) =
      addTestCase(createTestName("should ", name), test, defaultTestCaseConfig, TestType.Test)

  fun should(name: String) = Testbuilder { test, config -> addTestCase(createTestName("should ", name), test, config, TestType.Test) }

   inner class Testbuilder(val register: (suspend TestContext.() -> Unit, TestCaseConfig) -> Unit) {
      @UseExperimental(ExperimentalTime::class)
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
         register(test, config)
      }
   }

  @KotestDsl
  inner class ShouldScope(val context: TestContext) {

    suspend operator fun String.invoke(init: suspend ShouldScope.() -> Unit) =
        context.registerTestCase(this, this@AbstractShouldSpec, { this@AbstractShouldSpec.ShouldScope(this).init() }, this@AbstractShouldSpec.defaultTestCaseConfig, TestType.Container)

    suspend fun should(name: String, test: suspend TestContext.() -> Unit) =
        context.registerTestCase(createTestName("should ", name), this@AbstractShouldSpec, test, this@AbstractShouldSpec.defaultTestCaseConfig, TestType.Test)

     inner class Testbuilder(val register: suspend (suspend TestContext.() -> Unit, TestCaseConfig) -> Unit) {
        @UseExperimental(ExperimentalTime::class)
        suspend fun config(
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
           register(test, config)
        }
     }

    suspend fun should(name: String) = Testbuilder { test, config -> context.registerTestCase(createTestName("should ", name), this@AbstractShouldSpec, test, config, TestType.Test) }
  }
}
