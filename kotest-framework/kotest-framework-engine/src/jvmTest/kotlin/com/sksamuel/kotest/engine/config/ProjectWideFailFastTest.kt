package com.sksamuel.kotest.engine.config

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class ProjectWideFailFastTest : FunSpec() {
   init {
      val c = ProjectConfiguration()
      c.projectWideFailFast = true
      val listener = CollectingTestEngineListener()
      TestEngineLauncher(listener)
         .withConfiguration(c)
         .withClasses(A::class, B::class)
         .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
         .launch()
      listener.result("a")!!.isError.shouldBeTrue()
      listener.names shouldBe listOf("a", "b")
   }
}

private class A : FunSpec({

   test("a") {
      error("boom")
   }

   test("b") {
   }
})

private class B : FunSpec({

   test("c") {
   }

   test("d") {
   }
})
