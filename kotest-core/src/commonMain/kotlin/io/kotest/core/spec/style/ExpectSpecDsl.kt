package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.SpecDsl
import io.kotest.core.test.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface ExpectSpecDsl : SpecDsl {

   fun context(name: String, test: suspend ExpectScope.() -> Unit) =
      addTest(
         createTestName("Context: ", name),
         { ExpectScope(this, this@ExpectSpecDsl).test() },
         defaultConfig(),
         TestType.Container
      )

   class TestBuilder(val context: TestContext, val name: String, private val dsl: SpecDsl) {

      @OptIn(ExperimentalTime::class)
      suspend fun config(
         enabled: Boolean? = null,
         timeout: Duration? = null,
         tags: Set<Tag>? = null,
         extensions: List<TestCaseExtension>? = null,
         test: suspend TestContext.() -> Unit
      ) {
         val config = dsl.defaultConfig().deriveTestConfig(enabled, tags, extensions, timeout)
         context.registerTestCase(name, test, config, TestType.Test)
      }
   }
}

@KotestDsl
class ExpectScope(val context: TestContext, private val dsl: ExpectSpecDsl) {

   suspend fun context(name: String, test: suspend ExpectScope.() -> Unit) =
      context.registerTestCase(
         createTestName("Context: ", name),
         { ExpectScope(this, this@ExpectScope.dsl).test() },
         dsl.defaultConfig(),
         TestType.Container
      )

   suspend fun expect(name: String, test: suspend TestContext.() -> Unit) =
      context.registerTestCase(createTestName("Expect: ", name), test, dsl.defaultConfig(), TestType.Test)

   fun expect(name: String) = ExpectSpecDsl.TestBuilder(context, createTestName("Expect: ", name), dsl)
}
