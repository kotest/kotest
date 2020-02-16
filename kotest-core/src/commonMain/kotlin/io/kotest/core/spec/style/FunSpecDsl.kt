package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.SpecDsl
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.deriveTestConfig
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Defines the DSL for creating tests in the 'FunSpec' style
 */
@UseExperimental(ExperimentalTime::class)
interface FunSpecDsl : SpecDsl {

   class TestBuilder(
      private val name: String,
      private val spec: FunSpecDsl
   ) {
      fun config(
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
            spec.defaultConfig().deriveTestConfig(enabled, tags, extensions, timeout, enabledIf, invocations, threads)
         spec.addTest(name, test, config, TestType.Test)
      }
   }

   fun context(name: String, init: suspend ContextScope.() -> Unit) {
      addTest(name, { ContextScope(this, this@FunSpecDsl).init() }, defaultConfig(), TestType.Container)
   }

   @KotestDsl
   class ContextScope(private val context: TestContext, private val spec: FunSpecDsl) {

      suspend fun context(name: String, init: suspend ContextScope.() -> Unit) {
         context.registerTestCase(
            name,
            { ContextScope(this, this@ContextScope.spec).init() },
            spec.defaultConfig(),
            TestType.Container
         )
      }

      inner class TestBuilder(val name: String) {
         @UseExperimental(ExperimentalTime::class)
         suspend fun config(
            enabled: Boolean? = null,
            tags: Set<Tag>? = null,
            timeout: Duration? = null,
            extensions: List<TestCaseExtension>? = null,
            enabledIf: EnabledIf?,
            test: suspend TestContext.() -> Unit
         ) {
            val config = spec.defaultConfig()
               .deriveTestConfig(enabled, tags, extensions, timeout, enabledIf)
            context.registerTestCase(name, test, config, TestType.Test)
         }
      }

      fun test(name: String) = TestBuilder(name)

      suspend fun test(name: String, test: suspend TestContext.() -> Unit) =
         context.registerTestCase(name, test, spec.defaultConfig(), TestType.Test)
   }

   fun test(name: String) = TestBuilder(name, this)

   /**
    * Adds a new root test case, with the given name and test function, using
    * the default test case config for this builder.
    */
   fun test(name: String, test: suspend TestContext.() -> Unit) =
      addTest(name, test, defaultConfig(), TestType.Test)

}
