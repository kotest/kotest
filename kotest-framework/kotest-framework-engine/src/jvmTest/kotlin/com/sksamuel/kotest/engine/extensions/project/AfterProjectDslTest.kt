package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.config.configuration
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

class AfterProjectDslTest : FunSpec({

   test("afterProject in spec should be fired") {

      var fired = false

      configuration.registerExtensions(object : ProjectListener {
         override suspend fun afterProject() {
            fired = true
         }
      })

      KotestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withSpec(DummySpec6::class)
         .async()

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
