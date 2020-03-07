package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.spec.SpecDsl
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.*
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
@OptIn(ExperimentalTime::class)
interface WordSpecDsl : SpecDsl {

   infix fun String.should(init: suspend WordScope.() -> Unit) =
      addTest(
         "$this should",
         { WordScope(this, this@WordSpecDsl).init() },
         defaultConfig(),
         TestType.Container
      )

   infix fun String.When(init: suspend WhenContext.() -> Unit) = addWhenContext(this, init)
   infix fun String.`when`(init: suspend WhenContext.() -> Unit) = addWhenContext(this, init)

   private fun addWhenContext(name: String, init: suspend WhenContext.() -> Unit) {
      addTest(
         "$name when",
         { WhenContext(this, this@WordSpecDsl).init() },
         defaultConfig(),
         TestType.Container
      )
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
         val config = dsl.defaultConfig().deriveTestConfig(enabled, tags, extensions, timeout)
         context.registerTestCase(
            this,
            { FinalTestContext(this).test() },
            config,
            TestType.Test
         )
      }

      suspend infix operator fun String.invoke(test: suspend FinalTestContext.() -> Unit) {
         context.registerTestCase(
            this,
            { FinalTestContext(this).test() },
            dsl.defaultConfig(),
            TestType.Test
         )
      }

      // we need to override the should method to stop people nesting a should inside a should
      @Suppress("UNUSED_PARAMETER")
      @Deprecated("A should block can only be used at the top level", ReplaceWith("{}"), level = DeprecationLevel.ERROR)
      infix fun String.should(init: () -> Unit) = Unit
   }

   @KotestDsl
   class WhenContext(val context: TestContext, val dsl: WordSpecDsl) {

      suspend infix fun String.Should(test: suspend WordScope.() -> Unit) = addShouldContext(this, test)
      suspend infix fun String.should(test: suspend WordScope.() -> Unit) = addShouldContext(this, test)

      private suspend fun addShouldContext(name: String, test: suspend WordScope.() -> Unit) {
         context.registerTestCase(
            "$name should",
            { WordScope(this, this@WhenContext.dsl).test() },
            dsl.defaultConfig(),
            TestType.Container
         )
      }

   }

   @KotestDsl
   // this context is used so we can add the deprecated should method to stop nesting a should inside a should
   class FinalTestContext(val context: TestContext) : TestContext() {

      override suspend fun registerTestCase(nested: NestedTest) {
         context.registerTestCase(nested)
      }

      override val coroutineContext: CoroutineContext = context.coroutineContext
      override val testCase: TestCase = context.testCase

      // we need to override the should method to stop people nesting a should inside a should
      @Deprecated("A should block can only be used at the top level", ReplaceWith("{}"), level = DeprecationLevel.ERROR)
      infix fun String.should(init: () -> Unit) = { init() }
   }
}
