package com.sksamuel.kotest.engine.objects

import io.kotest.core.config.configuration
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

class ObjectSpecTest : FunSpec() {
   init {
      test("object specs should be supported") {

         var fired = false

         configuration.registerListener(object : ProjectListener {
            override suspend fun afterProject() {
               fired = true
            }
         })

         KotestEngineLauncher()
            .withListener(NoopTestEngineListener)
            .withSpec(DummyObjectSpec::class)
            .async()

         fired shouldBe true
      }
   }
}

private class DummyObjectSpec : FunSpec({
   test("foo") {}
})
