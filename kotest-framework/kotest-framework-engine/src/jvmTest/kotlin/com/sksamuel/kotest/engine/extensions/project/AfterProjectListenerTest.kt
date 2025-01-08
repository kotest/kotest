package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class AfterProjectListenerTest : FunSpec({

   test("ProjectListener's afterProject method should be fired") {

      var fired = false

      val c = ProjectConfiguration()
      c.registry.add(object : ProjectListener {
         override suspend fun afterProject() {
            fired = true
         }
      })

      TestEngineLauncher(NoopTestEngineListener)
         .withClasses(DummySpec4::class)
         .withProjectConfig(c)
         .launch()

      fired shouldBe true

   }

   test("AfterProjectListener's afterProject method should be fired") {

      var fired = false

      val c = ProjectConfiguration()
      c.registry.add(object : AfterProjectListener {
         override suspend fun afterProject() {
            fired = true
         }
      })

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
