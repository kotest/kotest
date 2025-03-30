package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

@EnabledIf(NotMacOnGithubCondition::class)
class BeforeProjectListenerTest : FunSpec({

   test("ProjectListener's beforeProject method should be fired") {

      var fired = false

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(object : ProjectListener {
            override suspend fun beforeProject() {
               fired = true
            }
         })
      }

      TestEngineLauncher(NoopTestEngineListener)
         .withClasses(DummySpec5::class)
         .withProjectConfig(c)
         .launch()

      fired shouldBe true
   }

   test("BeforeProjectListener's beforeProject method should be fired") {

      var fired = false

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(object : BeforeProjectListener {
            override suspend fun beforeProject() {
               fired = true
            }
         })
      }

      TestEngineLauncher(NoopTestEngineListener)
         .withClasses(DummySpec5::class)
         .withProjectConfig(c)
         .launch()

      fired shouldBe true

   }
})

private class DummySpec5 : FunSpec({
   test("foo") {}
})
