package com.sksamuel.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.applyConfigFromProjectConfig
import io.kotest.engine.config.applyConfigFromSystemProperties
import io.kotest.extensions.system.OverrideMode
import io.kotest.extensions.system.withEnvironment
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe

private const val key = KotestEngineProperties.tagInheritance

@Isolate
class ApplyTagInheritanceConfigTest : FunSpec({
   test("tag inheritance can come from sys props") {
      val config = ProjectConfiguration()

      config.tagInheritance shouldBe false

      withEnvironment(key, "false", OverrideMode.SetOrOverride) {
         withSystemProperty(key, "true", OverrideMode.SetOrOverride) {
            applyConfigFromSystemProperties(config)
         }
      }

      config.tagInheritance shouldBe true
   }

   test("tag inheritance can come from env vars with dots in name") {
      val config = ProjectConfiguration()

      config.tagInheritance shouldBe false

      withEnvironment(key, "true", OverrideMode.SetOrOverride) {
         applyConfigFromSystemProperties(config)
      }

      config.tagInheritance shouldBe true
   }

   test("tag inheritance can come from env vars with underscores in name") {
      val config = ProjectConfiguration()

      config.tagInheritance shouldBe false

      withEnvironment(key.replace('.', '_'), "TRUE", OverrideMode.SetOrOverride) {
         applyConfigFromSystemProperties(config)
      }

      config.tagInheritance shouldBe true
   }

   test("Tag inheritance can come from AbstractProjectConfig") {
      val config = ProjectConfiguration()

      config.tagInheritance shouldBe false

      applyConfigFromProjectConfig(object : AbstractProjectConfig() {
         override val tagInheritance = true
      }, config)

      config.tagInheritance shouldBe true
   }
})
