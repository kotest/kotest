package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Description
import io.kotlintest.Tag
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.TestType
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
      addTestCase(this + " should", { this@AbstractWordSpec.WordScope(this).init() }, defaultTestCaseConfig, TestType.Container)

  @KotlinTestDsl
  inner class WordScope(val context: TestContext) {

    fun String.config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: FinalTestContext.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: this@AbstractWordSpec.defaultTestCaseConfig.enabled,
          invocations ?: this@AbstractWordSpec.defaultTestCaseConfig.invocations,
          timeout ?: this@AbstractWordSpec.defaultTestCaseConfig.timeout,
          threads ?: this@AbstractWordSpec.defaultTestCaseConfig.threads,
          tags ?: this@AbstractWordSpec.defaultTestCaseConfig.tags,
          extensions ?: this@AbstractWordSpec.defaultTestCaseConfig.extensions)
      context.registerTestCase(this, this@AbstractWordSpec, { FinalTestContext(this).test() }, config, TestType.Test)
    }

    infix operator fun String.invoke(test: FinalTestContext.() -> Unit) =
        context.registerTestCase(this, this@AbstractWordSpec, { FinalTestContext(this).test() }, this@AbstractWordSpec.defaultTestCaseConfig, TestType.Test)

    // we need to override the should method to stop people nesting a should inside a should
    @Deprecated("A should block can only be used at the top level", ReplaceWith("{}"), level = DeprecationLevel.ERROR)
    infix fun String.should(init: () -> Unit) = { init() }
  }

  @KotlinTestDsl
  inner class FinalTestContext(val context: TestContext) : TestContext() {
    override fun description(): Description = context.description()
    override fun registerTestCase(testCase: TestCase) = context.registerTestCase(testCase)

    // we need to override the should method to stop people nesting a should inside a should
    @Deprecated("A should block can only be used at the top level", ReplaceWith("{}"), level = DeprecationLevel.ERROR)
    infix fun String.should(init: () -> Unit) = { init() }
  }
}