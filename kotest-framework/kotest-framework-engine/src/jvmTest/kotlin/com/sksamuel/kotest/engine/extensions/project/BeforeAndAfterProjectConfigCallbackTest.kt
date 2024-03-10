package com.sksamuel.kotest.engine.extensions.project

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

var beforeAfterProject = ""
var beforeAfterAll = ""

class BeforeAndAfterProjectConfigCallbackTest : WordSpec() {
   init {

      val config = object : AbstractProjectConfig() {

         override suspend fun beforeProject() {
            beforeAfterProject += "before"
         }

         override fun beforeAll() {
            beforeAfterAll += "beforeall"
         }

         override suspend fun afterProject() {
            beforeAfterProject += "after"
         }

         override fun afterAll() {
            beforeAfterAll += "afterall"
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

         "beforeAll / afterAll" {
            beforeAfterAll = ""
            TestEngineLauncher(NoopTestEngineListener)
               // two classes so we know these callbacks are only invoked once
               .withClasses(A::class, B::class)
               .withProjectConfig(config)
               .launch()
            beforeAfterAll shouldBe "beforeallabafterall"
         }
      }
   }
}

class A : FunSpec() {
   init {
      test("a") {
         beforeAfterProject += "a"
         beforeAfterAll += "a"
      }
   }
}

class B : FunSpec() {
   init {
      test("b") {
         beforeAfterProject += "b"
         beforeAfterAll += "b"
      }
   }
}
