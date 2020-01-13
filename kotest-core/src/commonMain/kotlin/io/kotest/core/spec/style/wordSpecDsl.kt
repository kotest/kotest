package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.spec.SpecDsl
import io.kotest.core.specs.KotestDsl
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.extensions.TestCaseExtension
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Defines the DSL for creating tests in the 'WordSpec' style.
 *
 * Example:
 *
 * "some test" should {
 *    "do something" {
 *      // test here
 *    }
 * }
 *
 */
@Suppress("FunctionName")
@UseExperimental(ExperimentalTime::class)
interface WordSpecDsl : SpecDsl {

   infix fun String.should(init: suspend WordScope.() -> Unit) =
      addTest(
         "$this should",
         { WordScope(this, this@WordSpecDsl).init() },
         defaultTestCaseConfig,
         TestType.Container
      )

   infix fun String.When(init: suspend WhenContext.() -> Unit) = addWhenContext(this, init)
   infix fun String.`when`(init: suspend WhenContext.() -> Unit) = addWhenContext(this, init)

   private fun addWhenContext(name: String, init: suspend WhenContext.() -> Unit) {
      addTest("$name when", { WhenContext(
         this,
         this@WordSpecDsl
      ).init() }, defaultTestCaseConfig, TestType.Container)
   }

   @KotestDsl
   class WordScope(
      private val context: TestContext,
      private val dsl: WordSpecDsl
   ) {

      suspend fun String.config(
         enabled: Boolean? = null,
         timeout: Duration? = null,
         tags: Set<Tag>? = null,
         extensions: List<TestCaseExtension>? = null,
         test: suspend FinalTestContext.() -> Unit
      ) {
         val config = TestCaseConfig(
            enabled = enabled ?: dsl.defaultTestCaseConfig.enabled,
            timeout = timeout ?: dsl.defaultTestCaseConfig.timeout,
            tags = tags ?: dsl.defaultTestCaseConfig.tags,
            extensions = extensions ?: dsl.defaultTestCaseConfig.extensions
         )
         context.registerTestCase(
            this,
            { FinalTestContext(this).test() },
            config,
            TestType.Test
         )
      }

      suspend infix operator fun String.invoke(test: suspend FinalTestContext.() -> Unit) =
         context.registerTestCase(
            this,
            { FinalTestContext(this).test() },
            dsl.defaultTestCaseConfig,
            TestType.Test
         )

      // we need to override the should method to stop people nesting a should inside a should
      @Deprecated("A should block can only be used at the top level", ReplaceWith("{}"), level = DeprecationLevel.ERROR)
      infix fun String.should(init: () -> Unit) = { init() }
   }

   @KotestDsl
   class WhenContext(val context: TestContext, val dsl: WordSpecDsl) {

      suspend infix fun String.Should(test: suspend WordScope.() -> Unit) = addShouldContext(this, test)
      suspend infix fun String.should(test: suspend WordScope.() -> Unit) = addShouldContext(this, test)

      private suspend fun addShouldContext(name: String, test: suspend WordScope.() -> Unit) {
         context.registerTestCase(
            "$name should",
            { WordScope(this, this@WhenContext.dsl).test() },
            dsl.defaultTestCaseConfig,
            TestType.Container
         )
      }

   }

   @KotestDsl
   class FinalTestContext(val context: TestContext) : TestContext() {

      override suspend fun registerTestCase(test: NestedTest) = context.registerTestCase(test)
      override val coroutineContext: CoroutineContext = context.coroutineContext

      // we need to override the should method to stop people nesting a should inside a should
      @Deprecated(
         "A should block can only be used at the top level",
         ReplaceWith("{}"),
         level = DeprecationLevel.ERROR
      )
      infix fun String.should(init: () -> Unit) = { init() }
   }
}
