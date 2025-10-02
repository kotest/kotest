package com.sksamuel.kotest.engine.extensions

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.InstantiationListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

class InstantiationListenerTest : FunSpec() {
   init {

      test("instantation listener should be detected from project config") {

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(MyInstantiationListener)
         }

         MyInstantiationListener.fired shouldBe null

         TestEngineLauncher()
            .withListener(NoopTestEngineListener)
            .withClasses(DummySpec7::class)
            .withProjectConfig(c)
            .launch()

         MyInstantiationListener.fired shouldBe "com.sksamuel.kotest.engine.extensions.DummySpec7"
      }
   }
}

object MyInstantiationListener : InstantiationListener {
   var fired: String? = null
   override suspend fun specInstantiated(spec: Spec) {
      fired = spec::class.java.name
   }
}

private class DummySpec7 : FunSpec({
   test("a") {}
})
