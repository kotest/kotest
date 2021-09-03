package com.sksamuel.kotest.engine.listeners.project

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

class BeforeAndAfterProjectCallbackTest : WordSpec() {
   init {
       "project config" should {
          "call beforeProject" {
             MyConfigGlobalState.beforeProjectCallCount = 0
             KotestEngineLauncher()
                .withListener(NoopTestEngineListener)
                .withSpecs(listOf(MyTest::class))
                .launch()
          }

          "call beforeAll" {
             MyConfigGlobalState.beforeAllCallCount = 0
             KotestEngineLauncher()
                .withListener(NoopTestEngineListener)
                .withSpecs(listOf(MyTest::class))
                .launch()
          }

          "call afterProject" {
             MyConfigGlobalState.afterProjectCallCount = 0
             KotestEngineLauncher()
                .withListener(NoopTestEngineListener)
                .withSpecs(listOf(MyTest::class))
                .launch()
             MyConfigGlobalState.afterProjectCallCount shouldBe 1
          }

          "call afterAll" {
             MyConfigGlobalState.afterAllCallCount = 0
             KotestEngineLauncher()
                .withListener(NoopTestEngineListener)
                .withSpecs(listOf(MyTest::class))
                .launch()
             MyConfigGlobalState.afterAllCallCount shouldBe 1
          }
       }
   }
}

private class MyTest : FunSpec() {
   init {
      test("beforeProject") {
         MyConfigGlobalState.beforeProjectCallCount shouldBe 1
      }

      test("beforeAll") {
         MyConfigGlobalState.beforeAllCallCount shouldBe 1
      }

      test("afterProject") {
         MyConfigGlobalState.afterProjectCallCount shouldBe 0
      }

      test("aafterAll") {
         MyConfigGlobalState.afterAllCallCount shouldBe 0
      }
   }
}
