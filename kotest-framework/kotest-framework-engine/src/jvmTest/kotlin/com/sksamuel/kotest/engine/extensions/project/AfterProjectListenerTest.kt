package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.config.configuration
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

class AfterProjectListenerTest : FunSpec({

   test("ProjectListener's afterProject method should be fired") {

      var fired = false

      configuration.registerListener(object : ProjectListener {
         override suspend fun afterProject() {
            fired = true
         }
      })

      KotestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withSpec(DummySpec4::class)
         .launch()

      fired shouldBe true

   }

   test("AfterProjectListener's afterProject method should be fired") {

      var fired = false

      configuration.registerListener(object : AfterProjectListener {
         override suspend fun afterProject() {
            fired = true
         }
      })

      KotestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withSpec(DummySpec4::class)
         .launch()

      fired shouldBe true

   }
})

private class DummySpec4 : FunSpec({
   test("foo") {}
})
