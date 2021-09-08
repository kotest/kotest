package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.config.configuration
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

class BeforeProjectListenerTest : FunSpec({

   test("ProjectListener's beforeProject method should be fired") {

      var fired = false

      configuration.registerExtensions(object : ProjectListener {
         override suspend fun beforeProject() {
            fired = true
         }
      })

      KotestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withSpec(DummySpec5::class)
         .async()

      fired shouldBe true

   }

   test("BeforeProjectListener's beforeProject method should be fired") {

      var fired = false

      configuration.registerExtensions(object : BeforeProjectListener {
         override suspend fun beforeProject() {
            fired = true
         }
      })

      KotestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withSpec(DummySpec5::class)
         .async()

      fired shouldBe true

   }
})

private class DummySpec5 : FunSpec({
   test("foo") {}
})
