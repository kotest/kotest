package com.sksamuel.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.LogLevel
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineProperties
import io.kotest.engine.config.applyConfigFromProjectConfig
import io.kotest.engine.config.applyConfigFromSystemProperties
import io.kotest.extensions.system.OverrideMode
import io.kotest.extensions.system.withEnvironment
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe

private const val key = KotestEngineProperties.logLevel

@io.kotest.core.annotation.Isolate
class ApplyConfigTest : FunSpec({
   test("log level can come from sys props") {
      val expected = LogLevel.Info
      val config = ProjectConfiguration()

      config.logLevel shouldBe LogLevel.Off

      withEnvironment(key, LogLevel.Error.name, OverrideMode.SetOrOverride) {
         withSystemProperty(key, expected.name, OverrideMode.SetOrOverride) {
            applyConfigFromSystemProperties(config)
         }
      }

      config.logLevel shouldBe expected
   }

   test("log level can come from env vars with dots in name") {
      val expected = LogLevel.Info
      val config = ProjectConfiguration()

      config.logLevel shouldBe LogLevel.Off

      withEnvironment(key, expected.name, OverrideMode.SetOrOverride) {
         applyConfigFromSystemProperties(config)
      }

      config.logLevel shouldBe expected
   }

   test("log level can come from env vars with underscores in name") {
      val expected = LogLevel.Info
      val config = ProjectConfiguration()

      config.logLevel shouldBe LogLevel.Off

      withEnvironment(key.replace('.', '_'), expected.name, OverrideMode.SetOrOverride) {
         applyConfigFromSystemProperties(config)
      }

      config.logLevel shouldBe expected
   }

   test("log level can come from AbstractProjectConfig") {
      val expected = LogLevel.Info
      val config = ProjectConfiguration()

      config.logLevel shouldBe LogLevel.Off

      applyConfigFromProjectConfig(object : AbstractProjectConfig() {
         override val logLevel = expected
      }, config)

      config.logLevel shouldBe expected
   }
})
