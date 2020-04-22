package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.SpecDsl
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createTestName
import io.kotest.core.test.deriveTestConfig
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

   fun xcontext(name: String, test: suspend ExpectScope.() -> Unit) =
      addTest(
         createTestName("Context: ", name),
         { ExpectScope(this, this@ExpectSpecDsl).test() },
         defaultConfig().copy(enabled = false),
         TestType.Container
      )

   class TestBuilder(
      val context: TestContext,
      val name: String,
      private val dsl: SpecDsl,
      private val xdisabled: Boolean
   ) {

      @OptIn(ExperimentalTime::class)
      suspend fun config(
         enabled: Boolean? = null,
         invocations: Int? = null,
         threads: Int? = null,
         tags: Set<Tag>? = null,
         timeout: Duration? = null,
         extensions: List<TestCaseExtension>? = null,
         enabledIf: EnabledIf? = null,
         test: suspend TestContext.() -> Unit
      ) {
         val config =
            dsl.defaultConfig().deriveTestConfig(enabled, tags, extensions, timeout, enabledIf, invocations, threads)
         val config2 = if (xdisabled) config.copy(enabled = false) else config
         context.registerTestCase(name, test, config2, TestType.Test)
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

   suspend fun xcontext(name: String, test: suspend ExpectScope.() -> Unit) =
      context.registerTestCase(
         createTestName("Context: ", name),
         { ExpectScope(this, this@ExpectScope.dsl).test() },
         dsl.defaultConfig().copy(enabled = false),
         TestType.Container
      )

   suspend fun expect(name: String, test: suspend TestContext.() -> Unit) =
      context.registerTestCase(createTestName("Expect: ", name), test, dsl.defaultConfig(), TestType.Test)

   suspend fun xexpect(name: String, test: suspend TestContext.() -> Unit) =
      context.registerTestCase(
         createTestName("Expect: ", name),
         test,
         dsl.defaultConfig().copy(enabled = false),
         TestType.Test
      )

   fun expect(name: String) = ExpectSpecDsl.TestBuilder(context, createTestName("Expect: ", name), dsl, false)
   fun xexpect(name: String) = ExpectSpecDsl.TestBuilder(context, createTestName("Expect: ", name), dsl, true)
}
