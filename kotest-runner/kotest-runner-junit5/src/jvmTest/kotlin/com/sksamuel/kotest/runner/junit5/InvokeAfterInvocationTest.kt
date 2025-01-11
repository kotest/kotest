package com.sksamuel.kotest.runner.junit5

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

class InvokeAfterInvocationTest : FunSpec({
   test("should execute all afterInvocation blocks") {
      EngineTestKit.engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(DiscoverySelectors.selectClass(AfterInvocationExample::class.java))
         .configurationParameter("allow_private", "true")
         .execute()

      AfterInvocationExample.count shouldBe 30
   }
})

private class AfterInvocationExample : FunSpec() {

   companion object {
      var count = 0
   }

   init {
      afterInvocation { _, _ -> count++ } // adds +1 10 times
      afterInvocation { _, _ -> count++ } // adds +1 10 times
      test("my case").config(invocations = 10) {
         count++ // adds +1 10 times
         true shouldBe true
      } // should be 30 in total
   }
}
