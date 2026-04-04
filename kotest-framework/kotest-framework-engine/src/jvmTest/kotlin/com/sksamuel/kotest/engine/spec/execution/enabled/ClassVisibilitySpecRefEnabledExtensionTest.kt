package com.sksamuel.kotest.engine.spec.execution.enabled

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.spec.execution.enabled.ClassVisibilitySpecRefEnabledExtension
import io.kotest.engine.spec.execution.enabled.EnabledOrDisabled
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
@Isolate
class ClassVisibilitySpecRefEnabledExtensionTest : FunSpec({

   test("ClassVisibilitySpecRefEnabledExtension should return enabled for public specs") {
      val config = object : AbstractProjectConfig() {
         override val ignorePrivateClasses: Boolean? = true
      }
      val resolver = ProjectConfigResolver(config)
      ClassVisibilitySpecRefEnabledExtension(resolver).isEnabled(SpecRef.Reference(PublicVisibilitySpec::class)) shouldBe EnabledOrDisabled.Enabled
   }

   test("ClassVisibilitySpecRefEnabledExtension should disable private specs when ignorePrivateClasses is true") {
      val config = object : AbstractProjectConfig() {
         override val ignorePrivateClasses: Boolean? = true
      }
      val resolver = ProjectConfigResolver(config)
      ClassVisibilitySpecRefEnabledExtension(resolver).isEnabled(SpecRef.Reference(PrivateVisibilitySpec::class)) shouldBe
         EnabledOrDisabled.Disabled("Disabled by ignorePrivateClasses")
   }

   test("ClassVisibilitySpecRefEnabledExtension should return enabled when KOTEST_TEST_ENABLED_OVERRIDE is set") {
      try {
         System.setProperty(KotestEngineProperties.KOTEST_TEST_ENABLED_OVERRIDE, "true")
         val config = object : AbstractProjectConfig() {
            override val ignorePrivateClasses: Boolean? = true
         }
         val resolver = ProjectConfigResolver(config)
         ClassVisibilitySpecRefEnabledExtension(resolver).isEnabled(SpecRef.Reference(PrivateVisibilitySpec::class)) shouldBe EnabledOrDisabled.Enabled
      } finally {
         System.getProperties().remove(KotestEngineProperties.KOTEST_TEST_ENABLED_OVERRIDE)
      }
   }
})

internal class PublicVisibilitySpec : FunSpec()
private class PrivateVisibilitySpec : FunSpec()
