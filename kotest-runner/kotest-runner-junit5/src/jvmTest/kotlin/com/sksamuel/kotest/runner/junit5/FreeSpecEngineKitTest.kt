package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit

@EnabledIf(NotMacOnGithubCondition::class)
class FreeSpecEngineKitTest : FunSpec({

   test("verify container events") {
      EngineTestKit
         .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
         .selectors(selectClass(FreeSpecSample::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.FreeSpecSample",
               "a simple failing test",
               "a simple passing test",
               "a simple erroring test",
               "a container with",
               "a failing test",
               "a passing test",
               "a erroring test",
               "an outer container with",
               "an inner container with",
               "a failing test",
               "a passing test",
               "a erroring test",
               "an empty outer container with",
               "an outer container that conatins",
               "an empty inner container",
               "an outer container with only passing tests",
               "a passing test 1",
               "a passing test 2"
            )
            aborted().shouldBeEmpty()
            skipped().shouldHaveNames("a simple skipped test", "a skipped test", "a skipped test")
            failed().shouldHaveNames(
               "a simple failing test",
               "a simple erroring test",
               "a failing test",
               "a erroring test",
               "a failing test",
               "a erroring test",
            )
            succeeded().shouldHaveNames(
               "a simple passing test",
               "a passing test",
               "a container with",
               "a passing test",
               "an inner container with",
               "an outer container with",
               "an empty outer container with",
               "an empty inner container",
               "an outer container that conatins",
               "a passing test 1",
               "a passing test 2",
               "an outer container with only passing tests",
               "com.sksamuel.kotest.runner.junit5.FreeSpecSample",
               "Kotest"
            )
            finished().shouldHaveNames(
               "a simple failing test",
               "a simple passing test",
               "a simple erroring test",
               "a failing test",
               "a passing test",
               "a erroring test",
               "a container with",
               "a failing test",
               "a passing test",
               "a erroring test",
               "an inner container with",
               "an outer container with",
               "an empty outer container with",
               "an empty inner container",
               "an outer container that conatins",
               "a passing test 1",
               "a passing test 2",
               "an outer container with only passing tests",
               "com.sksamuel.kotest.runner.junit5.FreeSpecSample",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "a simple failing test",
               "a simple passing test",
               "a simple erroring test",
               "a simple skipped test",
               "a container with",
               "a failing test",
               "a passing test",
               "a erroring test",
               "a skipped test",
               "an outer container with",
               "an inner container with",
               "a failing test",
               "a passing test",
               "a erroring test",
               "a skipped test",
               "an empty outer container with",
               "an outer container that conatins",
               "an empty inner container",
               "an outer container with only passing tests",
               "a passing test 1",
               "a passing test 2"
            )
         }
   }
})

private class FreeSpecSample : FreeSpec({

   "a simple failing test" {
      1 shouldBe 2
   }

   "a simple passing test" {
      1 shouldBe 1
   }

   "a simple erroring test" {
      throw RuntimeException()
   }

   "a simple skipped test".config(enabled = false) {}

   "a container with" - {
      "a failing test" {
         1 shouldBe 2
      }

      "a passing test" {
         1 shouldBe 1
      }

      "a erroring test" {
         throw RuntimeException()
      }

      "a skipped test".config(enabled = false) {}
   }

   "an outer container with" - {
      "an inner container with" - {
         "a failing test" {
            1 shouldBe 2
         }

         "a passing test" {
            1 shouldBe 1
         }

         "a erroring test" {
            throw RuntimeException()
         }

         "a skipped test".config(enabled = false) {}
      }
   }

   "an empty outer container with" {
   }

   "an outer container that conatins" - {
      "an empty inner container" {

      }
   }

   "an outer container with only passing tests" - {
      "a passing test 1" {
         1 shouldBe 1
      }
      "a passing test 2" {
         2 shouldBe 2
      }
   }

})
