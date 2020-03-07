package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.SpecDsl
import io.kotest.core.test.*
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
@OptIn(ExperimentalTime::class)
interface ShouldSpecDsl : SpecDsl {

   operator fun String.invoke(init: suspend ShouldScope.() -> Unit) =
      addTest(this, { ShouldScope(this, this@ShouldSpecDsl).init() }, defaultConfig(), TestType.Container)

   fun should(name: String, test: suspend TestContext.() -> Unit) =
      addTest(createTestName("should ", name), test, defaultConfig(), TestType.Test)

   fun should(name: String) = Testbuilder(this) { test, config ->
      addTest(createTestName("should ", name), test, config, TestType.Test)
   }

   class Testbuilder(
      private val specDsl: SpecDsl,
      private val register: (suspend TestContext.() -> Unit, TestCaseConfig) -> Unit
   ) {
      fun config(
         enabled: Boolean? = null,
         timeout: Duration? = null,
         tags: Set<Tag>? = null,
         extensions: List<TestCaseExtension>? = null,
         test: suspend TestContext.() -> Unit
      ) {
         val config = specDsl.defaultConfig().deriveTestConfig(enabled, tags, extensions, timeout)
         register(test, config)
      }
   }

}

@KotestDsl
@OptIn(ExperimentalTime::class)
class ShouldScope(val context: TestContext, private val dsl: SpecDsl) {

   suspend operator fun String.invoke(init: suspend ShouldScope.() -> Unit) =
      context.registerTestCase(
         this,
         { ShouldScope(this, this@ShouldScope.dsl).init() },
         dsl.defaultConfig(),
         TestType.Container
      )

   suspend fun should(name: String, test: suspend TestContext.() -> Unit) =
      context.registerTestCase(createTestName("should ", name), test, dsl.defaultConfig(), TestType.Test)

   inner class Testbuilder(val register: suspend (suspend TestContext.() -> Unit, TestCaseConfig) -> Unit) {
      suspend fun config(
         enabled: Boolean? = null,
         timeout: Duration? = null,
         tags: Set<Tag>? = null,
         extensions: List<TestCaseExtension>? = null,
         test: suspend TestContext.() -> Unit
      ) {
         val config = dsl.defaultConfig().deriveTestConfig(enabled, tags, extensions, timeout)
         register(test, config)
      }
   }

   suspend fun should(name: String) = Testbuilder { test, config ->
      context.registerTestCase(createTestName("should ", name), test, config, TestType.Test)
   }
}
