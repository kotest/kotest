package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class AfterProjectDslTest : FunSpec({

   test("afterProject in spec should be fired") {

      var fired = false

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(object : ProjectListener {
            override suspend fun afterProject() {
               fired = true
            }
         })
      }

      TestEngineLauncher(NoopTestEngineListener)
         .withClasses(DummySpec6::class)
         .withProjectConfig(c)
         .launch()

      fired shouldBe true

   }
})

private var fired = false

private class DummySpec6 : FunSpec({

   afterProject {
      fired = true
   }

   test("a") {}
})
