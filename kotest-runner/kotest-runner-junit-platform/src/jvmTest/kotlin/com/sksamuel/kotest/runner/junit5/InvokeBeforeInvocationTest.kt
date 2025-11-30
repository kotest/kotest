package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

@EnabledIf(LinuxOnlyGithubCondition::class)
class InvokeBeforeInvocationTest : FunSpec({
   test("should execute all beforeInvocation blocks") {
      EngineTestKit
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(DiscoverySelectors.selectClass(BeforeInvocationExample::class.java))
         .configurationParameter("allow_private", "true")
         .execute()

      BeforeInvocationExample.count shouldBe 30
   }
})

private class BeforeInvocationExample : FunSpec() {

   companion object {
      var count = 0
   }

   init {
      beforeInvocation { _, _ -> count++ } // adds +1 10 times
      beforeInvocation { _, _ -> count++ } // adds +1 10 times
      test("my case").config(invocations = 10) {
         count++ // adds +1 10 times
         true shouldBe true
      } // should be 30 in total
   }
}
