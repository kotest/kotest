package com.sksamuel.kotest.engine.config

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.LogLevel
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.matchers.shouldBe

@EnabledIf(NotMacOnGithubCondition::class)
@io.kotest.core.annotation.Isolate
class ApplyConfigTest : FunSpec({

   test("log level can come from AbstractProjectConfig") {
      val expected = LogLevel.Info
      val c = object : AbstractProjectConfig() {
         override val logLevel = expected
      }
      ProjectConfigResolver(c).logLevel() shouldBe expected
   }
})
