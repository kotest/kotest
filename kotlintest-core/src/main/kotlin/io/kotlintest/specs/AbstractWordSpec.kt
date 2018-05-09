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
 * "some test" should {
 *    "do something" {
 *      // test here
 *    }
 * }
 *
 */
abstract class AbstractWordSpec(body: AbstractWordSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  infix fun String.should(init: WordScope.() -> Unit) =
      addTestCase(this, { this@AbstractWordSpec.WordScope(this).init() }, defaultTestCaseConfig)

  @KotlinTestDsl
  inner class WordScope(val context: TestContext) {

    fun String.config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: TestContext.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: this@AbstractWordSpec.defaultTestCaseConfig.enabled,
          invocations ?: this@AbstractWordSpec.defaultTestCaseConfig.invocations,
          timeout ?: this@AbstractWordSpec.defaultTestCaseConfig.timeout,
          threads ?: this@AbstractWordSpec.defaultTestCaseConfig.threads,
          tags ?: this@AbstractWordSpec.defaultTestCaseConfig.tags,
          extensions ?: this@AbstractWordSpec.defaultTestCaseConfig.extensions)
      context.registerTestCase("should $this", this@AbstractWordSpec, test, config)
    }

    infix operator fun String.invoke(test: TestContext.() -> Unit) =
        context.registerTestCase("should $this", this@AbstractWordSpec, test, this@AbstractWordSpec.defaultTestCaseConfig)
  }
}