package com.sksamuel.kotest.junit5

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.shouldBe
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit

class FreeSpecEngineKitTest : FunSpec({

   test("verify container events") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(FreeSpecSample::class.java))
         .execute()
         .allEvents().apply {
            started().list().map { it.testDescriptor.displayName }.shouldContainExactly("Kotest")
            aborted().list().map { it.testDescriptor.displayName }.shouldBeEmpty()
            skipped().list().map { it.testDescriptor.displayName }.shouldBeEmpty()
            failed().list().map { it.testDescriptor.displayName }.shouldBeEmpty()
            succeeded().list().map { it.testDescriptor.displayName }.shouldContainExactly("Kotest")
            finished().list().map { it.testDescriptor.displayName }.shouldContainExactly("Kotest")
            dynamicallyRegistered().list().map { it.testDescriptor.displayName }.shouldContainExactly("")
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

   "an empty outer container with" - {
   }

   "an outer container that conatins" - {
      "an empty inner container" - {

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
