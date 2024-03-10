package com.sksamuel.kotest.runner.junit5

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit

class WordSpecEngineKitTest : FunSpec({

   beforeSpec {
      System.setProperty(KotestEngineProperties.includePrivateClasses, "true")
   }

   afterSpec {
      System.setProperty(KotestEngineProperties.includePrivateClasses, "false")
   }

   test("verify engine stats") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(WordSpecSample::class.java))
                  .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.WordSpecSample",
               "a container should",
               "fail a test",
               "pass a test",
               "error",
               "an empty when container should",
               "an empty should container should",
               "this when container when",
               "contain an empty should container should",
               "a when container with a failing test when",
               "with a should container should",
               "fail a test",
               "pass a test",
               "a when container when",
               "with a should container should",
               "pass a test",
               "a failing container should"
            )
            aborted().shouldBeEmpty()
            skipped().shouldHaveNames("skip a test", "skip a test")
            failed().shouldHaveNames(
               "fail a test",
               "error",
               "fail a test",
               "a failing container should"
            )
            succeeded().shouldHaveNames(
               "pass a test",
               "a container should",
               "an empty when container should",
               "an empty should container should",
               "contain an empty should container should",
               "this when container when",
               "pass a test",
               "with a should container should",
               "a when container with a failing test when",
               "pass a test",
               "with a should container should",
               "a when container when",
               "com.sksamuel.kotest.runner.junit5.WordSpecSample",
               "Kotest"
            )
            finished().shouldHaveNames(
               "fail a test",
               "pass a test",
               "error",
               "a container should",
               "an empty when container should",
               "an empty should container should",
               "contain an empty should container should",
               "this when container when",
               "fail a test",
               "pass a test",
               "with a should container should",
               "a when container with a failing test when",
               "pass a test",
               "with a should container should",
               "a when container when",
               "a failing container should",
               "com.sksamuel.kotest.runner.junit5.WordSpecSample",
               "Kotest",
            )
            dynamicallyRegistered().shouldHaveNames(
               "a container should",
               "skip a test",
               "fail a test",
               "pass a test",
               "error",
               "an empty when container should",
               "an empty should container should",
               "this when container when",
               "contain an empty should container should",
               "a when container with a failing test when",
               "with a should container should",
               "fail a test",
               "pass a test",
               "a when container when",
               "with a should container should",
               "pass a test",
               "skip a test",
               "a failing container should"
            )
         }
   }
})

private class WordSpecSample : WordSpec({

   "a container" should {
      "skip a test".config(enabled = false) {}
      "fail a test" { 1 shouldBe 2 }
      "pass a test" { 1 shouldBe 1 }
      "error" { throw RuntimeException() }
   }

   "an empty when container" should {

   }

   "an empty should container" should {

   }

   "this when container" `when` {
      "contain an empty should container" should {

      }
   }

   "a when container with a failing test" `when` {
      "with a should container" should {
         "fail a test" { 1 shouldBe 2 }
         "pass a test" { 1 shouldBe 1 }
      }
   }

   "a when container" `when` {
      "with a should container" should {
         "pass a test" { 1 shouldBe 1 }
         "skip a test".config(enabled = false) {}
      }
   }

   "a failing container" should {
      throw RuntimeException()
      "not reach this test" {}
   }
})
