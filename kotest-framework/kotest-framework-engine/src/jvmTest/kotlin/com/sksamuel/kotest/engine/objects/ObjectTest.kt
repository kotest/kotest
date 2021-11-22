package com.sksamuel.kotest.engine.objects

import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.MutableConfiguration
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

class ObjectSpecTest : FunSpec() {
   init {
      test("object specs should be supported") {

         var fired = false

         val c = MutableConfiguration()
         c.registry().add(object : ProjectListener {
            override suspend fun afterProject() {
               fired = true
            }
         })

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(DummyObjectSpec::class)
            .withConfiguration(c)
            .launch()

         fired shouldBe true
      }
   }
}

private class DummyObjectSpec : FunSpec({
   test("foo") {}
})
