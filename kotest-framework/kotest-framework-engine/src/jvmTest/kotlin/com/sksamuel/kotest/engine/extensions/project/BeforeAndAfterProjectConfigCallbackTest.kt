package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

var beforeAfterProject = ""

@EnabledIf(LinuxCondition::class)
class BeforeAndAfterProjectConfigCallbackTest : WordSpec() {
   init {

      val config = object : AbstractProjectConfig() {

         override suspend fun beforeProject() {
            beforeAfterProject += "before"
         }

         override suspend fun afterProject() {
            beforeAfterProject += "after"
         }
      }

      "project config" should {
         "beforeProject / afterProject" {
            beforeAfterProject = ""
            TestEngineLauncher(NoopTestEngineListener)
               // two classes so we know these callbacks are only invoked once
               .withClasses(A::class, B::class)
               .withProjectConfig(config)
               .launch()
            beforeAfterProject shouldBe "beforeabafter"
         }
      }
   }
}

private class A : FunSpec() {
   init {
      test("a") {
         beforeAfterProject += "a"
      }
   }
}

private class B : FunSpec() {
   init {
      test("b") {
         beforeAfterProject += "b"
      }
   }
}
