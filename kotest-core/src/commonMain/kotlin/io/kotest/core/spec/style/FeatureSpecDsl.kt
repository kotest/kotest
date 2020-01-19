package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.SpecDsl
import io.kotest.core.test.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface FeatureSpecDsl : SpecDsl {
   fun feature(name: String, init: suspend FeatureScope.() -> Unit) =
      addTest(
         createTestName("Feature: ", name),
         { FeatureScope(this, this@FeatureSpecDsl).init() },
         defaultConfig(),
         TestType.Container
      )
}

@UseExperimental(ExperimentalTime::class)
class ScenarioBuilder(val name: String, val context: TestContext, val dsl: SpecDsl) {
   suspend fun config(
      enabled: Boolean? = null,
      timeout: Duration? = null,
      tags: Set<Tag>? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      test: suspend TestContext.() -> Unit
   ) {
      val config = dsl.defaultConfig().deriveTestConfig(enabled, tags, extensions, timeout, enabledIf)
      context.registerTestCase(name, test, config, TestType.Test)
   }
}

@KotestDsl
class FeatureScope(val context: TestContext, private val dsl: SpecDsl) {

   @Deprecated("use nested 'feature' rather than 'and'", ReplaceWith("feature(name, init)"))
   suspend fun and(name: String, init: suspend FeatureScope.() -> Unit) =
      context.registerTestCase(
         createTestName("And: ", name),
         { FeatureScope(this, this@FeatureScope.dsl).init() },
         dsl.defaultConfig(),
         TestType.Container
      )

   suspend fun feature(name: String, init: suspend FeatureScope.() -> Unit) =
      context.registerTestCase(
         createTestName("Feature: ", name),
         { FeatureScope(this, this@FeatureScope.dsl).init() },
         dsl.defaultConfig(),
         TestType.Container
      )

   suspend fun scenario(name: String, test: suspend TestContext.() -> Unit) =
      context.registerTestCase(
         createTestName("Scenario: ", name),
         test,
         dsl.defaultConfig(),
         TestType.Test
      )

   fun scenario(name: String) = ScenarioBuilder(createTestName("Scenario: ", name), context, dsl)
}
