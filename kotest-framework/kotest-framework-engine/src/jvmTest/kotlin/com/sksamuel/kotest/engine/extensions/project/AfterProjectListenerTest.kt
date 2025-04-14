package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class AfterProjectListenerTest : FunSpec({

   test("ProjectListener's afterProject method should be fired") {

      var fired = false

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(object : ProjectListener {
            override suspend fun afterProject() {
               fired = true
            }
         })
      }

      TestEngineLauncher(NoopTestEngineListener)
         .withClasses(DummySpec4::class)
         .withProjectConfig(c)
         .launch()

      fired shouldBe true

   }

   test("AfterProjectListener's afterProject method should be fired") {

      var fired = false


      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(object : AfterProjectListener {
            override suspend fun afterProject() {
               fired = true
            }
         })
      }

      TestEngineLauncher(NoopTestEngineListener)
         .withClasses(DummySpec4::class)
         .withProjectConfig(c)
         .launch()

      fired shouldBe true

   }
})

private class DummySpec4 : FunSpec({
   test("foo") {}
})
