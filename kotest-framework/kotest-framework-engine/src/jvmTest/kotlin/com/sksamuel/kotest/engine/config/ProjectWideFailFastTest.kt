package com.sksamuel.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking

class ProjectWideFailFastTest : FunSpec() {
   init {

      val c = object : AbstractProjectConfig() {
         override val projectWideFailFast = true
      }

      val listener = CollectingTestEngineListener()
      runBlocking {
         TestEngineLauncher().withListener(listener)
            .withProjectConfig(c)
            .withSpecRefs(SpecRef.Reference(A::class), SpecRef.Reference(B::class))
            .execute()
      }

      listener.result("a").shouldNotBeNull().isError.shouldBeTrue()
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
