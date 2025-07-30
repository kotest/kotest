package com.sksamuel.kotest.matchers

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class AssertSoftlyTestLevelTest : FunSpec() {
   init {
      assertSoftly = true

      context("spec level assertSoftly should work for containers and tests") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(AssertSoftlyAtTestLevel::class)
            .launch()
         listener.tests.size shouldBe 2
         listener.errors shouldBe true
         listener.result("spec level assertSoftly should work on containers")!!.errorOrNull!!.message shouldContain "The following 2 assertions failed"
         listener.result("spec level assertSoftly should work on tests")!!.errorOrNull!!.message shouldContain "The following 2 assertions failed"
      }
   }
}

private class AssertSoftlyAtTestLevel : FunSpec() {
   init {
      assertSoftly = true

      context("spec level assertSoftly should work on containers") {
         1 shouldBe 2
         1 shouldBe 3
      }

      test("spec level assertSoftly should work on tests") {
         1 shouldBe 2
         1 shouldBe 3
      }
   }
}
