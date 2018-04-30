package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

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
abstract class AbstractShouldSpec(body: AbstractShouldSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  operator fun String.invoke(init: ShouldSpecContext.() -> Unit) =
      addTestCase(this, { ShouldSpecContext(this).init() }, defaultTestCaseConfig)

  fun should(name: String, test: TestContext.() -> Unit) =
      addTestCase("should $name", test, defaultTestCaseConfig)

  fun should(name: String) = ExpectsConfig({ test, config -> addTestCase("should $name", test, config) })

  inner class ExpectsConfig(val register: (TestContext.() -> Unit, TestCaseConfig) -> Unit) {
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
      register(test, config)
    }
  }

  inner class ShouldSpecContext(val context: TestContext) {

    operator fun String.invoke(init: ShouldSpecContext.() -> Unit) =
        context.registerTestScope(this, this@AbstractShouldSpec, { ShouldSpecContext(this).init() }, defaultTestCaseConfig)

    fun should(name: String, test: TestContext.() -> Unit) =
        context.registerTestScope("should $name", this@AbstractShouldSpec, test, defaultTestCaseConfig)

    fun should(name: String) = ExpectsConfig("should $name")

    inner class ExpectsConfig(val name: String) {
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
        context.registerTestScope("should $name", this@AbstractShouldSpec, test, config)
      }
    }
  }
}