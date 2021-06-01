package com.sksamuel.kotest.engine.listeners

import io.kotest.core.config.configuration
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

class BeforeProjectListenerTest : FunSpec({

   test("ProjectListener's afterProject method should be fired") {

      var fired = false

      configuration.registerListener(object : ProjectListener {
         override suspend fun beforeProject() {
            fired = true
         }
      })

      KotestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withSpec(DummySpec5::class)
         .launch()

      fired shouldBe true

   }

   test("AfterProjectListener's afterProject method should be fired") {

      var fired = false

      configuration.registerListener(object : BeforeProjectListener {
         override suspend fun beforeProject() {
            fired = true
         }
      })

      KotestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withSpec(DummySpec5::class)
         .launch()

      fired shouldBe true

   }
})

private class DummySpec5 : FunSpec({
   test("foo") {}
})
