package com.sksamuel.kotest.matchers

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class AssertSoftlyTestLevelTest : FunSpec() {
   init {
      assertSoftly = true

      context("test level assertSoftly should work at the container level") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(AssertSoftlyAtTestLevel::class)
            .withProjectConfig(ProjectConfiguration())
            .launch()
         listener.tests.size shouldBe 2
         listener.errors shouldBe true
         listener.result("test level assertSoftly should work at the container level")!!.errorOrNull!!.message shouldContain "The following 2 assertions failed"
         listener.result("test level assertSoftly should work at the test level")!!.errorOrNull!!.message shouldContain "The following 2 assertions failed"
      }
   }
}

private class AssertSoftlyAtTestLevel : FunSpec() {
   init {
      assertSoftly = true

      context("test level assertSoftly should work at the container level") {
         1 shouldBe 2
         1 shouldBe 3
      }

      test("test level assertSoftly should work at the test level") {
         1 shouldBe 2
         1 shouldBe 3
      }
   }
}
