package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.config.Configuration
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

class BeforeProjectListenerTest : FunSpec({

   test("ProjectListener's beforeProject method should be fired") {

      var fired = false

      val c = Configuration()
      c.registry().add(object : ProjectListener {
         override suspend fun beforeProject() {
            fired = true
         }
      })

      TestEngineLauncher(NoopTestEngineListener)
         .withClasses(DummySpec5::class)
         .withConfiguration(c)
         .launch()

      fired shouldBe true
   }

   test("BeforeProjectListener's beforeProject method should be fired") {

      var fired = false

      val c = Configuration()
      c.registry().add(object : BeforeProjectListener {
         override suspend fun beforeProject() {
            fired = true
         }
      })

      TestEngineLauncher(NoopTestEngineListener)
         .withClasses(DummySpec5::class)
         .withConfiguration(c)
         .launch()

      fired shouldBe true

   }
})

private class DummySpec5 : FunSpec({
   test("foo") {}
})
