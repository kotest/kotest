package com.sksamuel.kotest.engine.spec.execution.enabled

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.annotation.RequiresTag
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.spec.execution.enabled.EnabledOrDisabled
import io.kotest.engine.spec.execution.enabled.RequiresTagSpecRefEnabledExtension
import io.kotest.engine.tags.TagExpression
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
@Isolate
class RequiresTagSpecRefEnabledExtensionTest : ShouldSpec() {
   init {
      should("include any spec not annotated with @RequiresTag") {
         val c = object : AbstractProjectConfig() {}
         val p = ProjectConfigResolver(c)
         RequiresTagSpecRefEnabledExtension(p).isEnabled(SpecRef.Reference(RequiresNothingSpec::class)) shouldBe EnabledOrDisabled.Enabled
      }

      should("include any spec annotated with @RequiresTag when the tag is specified") {
         val c = object : AbstractProjectConfig() {
            override val extensions: List<Extension> = listOf(TagExtension { TagExpression("foo") })
         }
         val p = ProjectConfigResolver(c)
         RequiresTagSpecRefEnabledExtension(p).isEnabled(SpecRef.Reference(RequiresFooSpec::class)) shouldBe EnabledOrDisabled.Enabled
      }

      should("exclude any spec annotated with @RequiresTag when the tag is missing") {
         val c = object : AbstractProjectConfig() {}
         val p = ProjectConfigResolver(c)
         RequiresTagSpecRefEnabledExtension(p).isEnabled(SpecRef.Reference(RequiresBarSpec::class)) shouldBe
            EnabledOrDisabled.Disabled("Disabled by @RequiresTag (bar)")
      }

      should("return enabled for @RequiresTag spec when KOTEST_TEST_ENABLED_OVERRIDE is set") {
         try {
            System.setProperty(KotestEngineProperties.KOTEST_TEST_ENABLED_OVERRIDE, "true")
            val c = object : AbstractProjectConfig() {}
            val p = ProjectConfigResolver(c)
            RequiresTagSpecRefEnabledExtension(p).isEnabled(SpecRef.Reference(RequiresBarSpec::class)) shouldBe EnabledOrDisabled.Enabled
         } finally {
            System.getProperties().remove(KotestEngineProperties.KOTEST_TEST_ENABLED_OVERRIDE)
         }
      }
   }
}

private class RequiresNothingSpec : FunSpec()

@RequiresTag("foo")
private class RequiresFooSpec : FunSpec()

@RequiresTag("bar")
private class RequiresBarSpec : FunSpec()
