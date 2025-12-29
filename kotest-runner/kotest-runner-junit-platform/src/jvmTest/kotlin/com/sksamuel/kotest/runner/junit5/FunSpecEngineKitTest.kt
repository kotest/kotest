package com.sksamuel.kotest.runner.junit5

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit

class FunSpecEngineKitTest : FunSpec({

   test("verify engine stats") {
      EngineTestKit
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(FunSpecSample::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               KotestJunitPlatformTestEngine.ENGINE_NAME,
               "FunSpecSample",
               "a failing test",
               "a passing test",
               "an erroring test"
            )
            aborted().shouldBeEmpty()
            skipped().shouldHaveNames("a skipped test")
            failed().shouldHaveNames(
               "a failing test",
               "an erroring test",
            )
            succeeded().shouldHaveNames("a passing test", "FunSpecSample", "Kotest")
            finished().shouldHaveNames(
               "a failing test",
               "a passing test",
               "an erroring test",
               "FunSpecSample",
               KotestJunitPlatformTestEngine.ENGINE_NAME,
            )
            dynamicallyRegistered().shouldHaveNames(
               "a failing test",
               "a passing test",
               "an erroring test",
               "a skipped test"
            )
         }
   }
})

private class FunSpecSample : FunSpec({

   test("a failing test") {
      1 shouldBe 2
   }

   test("a passing test") {
      1 shouldBe 1
   }

   test("an erroring test") {
      throw RuntimeException()
   }

   test("a skipped test").config(enabled = false) {
   }

})
