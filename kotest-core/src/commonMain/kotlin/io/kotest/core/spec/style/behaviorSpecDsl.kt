@file:Suppress("FunctionName", "unused")

package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.SpecDsl
import io.kotest.core.test.createTestName
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.deriveTestConfig
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Suppress("FunctionName")
@UseExperimental(ExperimentalTime::class)
interface BehaviorSpecDsl : SpecDsl {

   fun Given(name: String, test: suspend GivenContext.() -> Unit) = addGivenContext(name, test)
   fun given(name: String, test: suspend GivenContext.() -> Unit) = addGivenContext(name, test)

   private fun addGivenContext(name: String, test: suspend GivenContext.() -> Unit) {
      addTest(
         createTestName("Given: ", name),
         { GivenContext(this, this@BehaviorSpecDsl).test() },
         defaultConfig(),
         TestType.Container
      )
   }

   @KotestDsl
   class TestScope(val name: String, val context: TestContext, val dsl: BehaviorSpecDsl) {
      suspend fun config(
         enabled: Boolean? = null,
         timeout: Duration? = null,
         tags: Set<Tag>? = null,
         extensions: List<TestCaseExtension>? = null,
         test: TestContext.() -> Unit
      ) {
         val config = dsl.defaultConfig().deriveTestConfig(enabled, tags, extensions, timeout)
         context.registerTestCase(name, { test.invoke(this) }, config, TestType.Test)
      }
   }

}

@KotestDsl
class ThenContext(val context: TestContext)

@KotestDsl
class GivenContext(val context: TestContext, private val dsl: BehaviorSpecDsl) {

   suspend fun And(name: String, test: suspend GivenAndContext.() -> Unit) = addAndContext(name, test)
   suspend fun and(name: String, test: suspend GivenAndContext.() -> Unit) = addAndContext(name, test)

   private suspend fun addAndContext(name: String, test: suspend GivenAndContext.() -> Unit) {
      context.registerTestCase(
         createTestName("And: ", name),
         { GivenAndContext(this, this@GivenContext.dsl).test() },
         dsl.defaultConfig(),
         TestType.Container
      )
   }

   suspend fun When(name: String, test: suspend WhenContext.() -> Unit) = addWhenContext(name, test)
   suspend fun `when`(name: String, test: suspend WhenContext.() -> Unit) = addWhenContext(name, test)

   private suspend fun addWhenContext(name: String, test: suspend WhenContext.() -> Unit) {
      context.registerTestCase(
         createTestName("When: ", name),
         { WhenContext(this, this@GivenContext.dsl).test() },
         dsl.defaultConfig(),
         TestType.Container
      )
   }

   suspend fun Then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)
   suspend fun then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)

   private suspend fun addThenContext(name: String, test: suspend ThenContext.() -> Unit) {
      context.registerTestCase(
         createTestName("Then: ", name),
         { ThenContext(this).test() },
         dsl.defaultConfig(),
         TestType.Container
      )
   }

   fun then(name: String) = BehaviorSpecDsl.TestScope(name, context, dsl)
}

@KotestDsl
class GivenAndContext(val context: TestContext, private val dsl: BehaviorSpecDsl) {
   suspend fun And(name: String, test: suspend GivenAndContext.() -> Unit) = addAndContext(name, test)
   suspend fun and(name: String, test: suspend GivenAndContext.() -> Unit) = addAndContext(name, test)

   private suspend fun addAndContext(name: String, test: suspend GivenAndContext.() -> Unit) {
      context.registerTestCase(
         createTestName("And: ", name),
         { GivenAndContext(this, this@GivenAndContext.dsl).test() },
         dsl.defaultConfig(),
         TestType.Container
      )
   }

   suspend fun When(name: String, test: suspend WhenContext.() -> Unit) = addWhenContext(name, test)
   suspend fun `when`(name: String, test: suspend WhenContext.() -> Unit) = addWhenContext(name, test)

   private suspend fun addWhenContext(name: String, test: suspend WhenContext.() -> Unit) {
      context.registerTestCase(
         createTestName("When: ", name),
         { WhenContext(this, this@GivenAndContext.dsl).test() },
         dsl.defaultConfig(),
         TestType.Container
      )
   }

   suspend fun Then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)
   suspend fun then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)

   private suspend fun addThenContext(name: String, test: suspend ThenContext.() -> Unit) {
      context.registerTestCase(
         createTestName("Then: ", name),
         { ThenContext(this).test() },
         dsl.defaultConfig(),
         TestType.Container
      )
   }

   fun then(name: String) = BehaviorSpecDsl.TestScope(name, context, dsl)
}


@KotestDsl
class WhenContext(val context: TestContext, private val dsl: BehaviorSpecDsl) {
   suspend fun And(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)
   suspend fun and(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)

   private suspend fun addAndContext(name: String, test: suspend WhenAndContext.() -> Unit) {
      context.registerTestCase(
         createTestName("And: ", name),
         { WhenAndContext(this, this@WhenContext.dsl).test() },
         dsl.defaultConfig(),
         TestType.Container
      )
   }

   suspend fun Then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)
   suspend fun then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)

   private suspend fun addThenContext(name: String, test: suspend ThenContext.() -> Unit) {
      context.registerTestCase(
         createTestName("Then: ", name),
         { ThenContext(this).test() },
         dsl.defaultConfig(),
         TestType.Test
      )
   }

   fun then(name: String) = BehaviorSpecDsl.TestScope(name, context, dsl)
}


@KotestDsl
class WhenAndContext(val context: TestContext, private val dsl: BehaviorSpecDsl) {
   suspend fun And(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)
   suspend fun and(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)

   private suspend fun addAndContext(name: String, test: suspend WhenAndContext.() -> Unit) {
      context.registerTestCase(
         createTestName("And: ", name),
         { WhenAndContext(this, this@WhenAndContext.dsl).test() },
         dsl.defaultConfig(),
         TestType.Container
      )
   }

   suspend fun Then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)
   suspend fun then(name: String, test: suspend ThenContext.() -> Unit) = addThenContext(name, test)

   private suspend fun addThenContext(name: String, test: suspend ThenContext.() -> Unit) {
      context.registerTestCase(
         createTestName("Then: ", name),
         { ThenContext(this).test() },
         dsl.defaultConfig(),
         TestType.Test
      )
   }

   fun then(name: String) = BehaviorSpecDsl.TestScope(name, context, dsl)
}
