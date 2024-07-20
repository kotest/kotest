package com.sksamuel.kotest.runner.junit5

import io.kotest.common.nonConstantFalse
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.AssertionMode
import io.kotest.inspectors.forAll
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine.Companion.EngineId
import io.kotest.runner.junit.platform.Segment
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.engine.discovery.DiscoverySelectors.selectUniqueId
import org.junit.platform.testkit.engine.EngineTestKit
import java.util.concurrent.atomic.AtomicInteger

class FeatureSpecEngineKitTest : FunSpec({

   test("verify engine events happy path") {
      listOf(
         selectClass(FeatureSpecHappyPathSample::class.java),
         selectUniqueId(UniqueId.forEngine(EngineId).append(Segment.Spec.value, FeatureSpecHappyPathSample::class.qualifiedName))
      ).forAll { selector ->
         EngineTestKit
            .engine("kotest")
            .selectors(selector)
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               started().shouldHaveNames(
                  "Kotest",
                  "com.sksamuel.kotest.runner.junit5.FeatureSpecHappyPathSample",
                  "1",
                  "1.1",
                  "1.2",
                  "1.2.1",
                  "1.2.2",
                  "1.2.2.1",
                  "1.2.2.2",
                  "2",
                  "2.1"
               )
               aborted().shouldBeEmpty()
               skipped().shouldHaveNames("2.1.2")
               failed().shouldHaveNames(
                  "1.2.2.2",
                  "2.1",
               )
               succeeded().shouldHaveNames(
                  "1.1",
                  "1.2.1",
                  "1.2.2.1",
                  "1.2.2",
                  "1.2",
                  "1",
                  "2",
                  "com.sksamuel.kotest.runner.junit5.FeatureSpecHappyPathSample",
                  "Kotest"
               )
               finished().shouldHaveNames(
                  "1.1",
                  "1.2.1",
                  "1.2.2.1",
                  "1.2.2.2",
                  "1.2.2",
                  "1.2",
                  "1",
                  "2.1",
                  "2",
                  "com.sksamuel.kotest.runner.junit5.FeatureSpecHappyPathSample",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "1",
                  "1.1",
                  "1.2",
                  "1.2.1",
                  "1.2.2",
                  "1.2.2.1",
                  "1.2.2.2",
                  "2",
                  "2.1",
                  "2.1.2"
               )
            }
      }
   }

   test("verify engine events all errors path") {
      listOf(
         selectClass(FeatureSpecSample::class.java),
         selectUniqueId(UniqueId.forEngine(EngineId).append(Segment.Spec.value, FeatureSpecSample::class.qualifiedName))
      ).forAll { selector ->
         EngineTestKit
            .engine("kotest")
            .selectors(selector)
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               started().shouldHaveNames(
                  "Kotest",
                  "com.sksamuel.kotest.runner.junit5.FeatureSpecSample",
                  "1",
                  "1.1",
                  "1.2",
                  "2"
               )
               aborted().shouldBeEmpty()
               skipped().shouldHaveNames()
               failed().shouldHaveNames(
                  "1.2", "2",
               )
               succeeded().shouldHaveNames(
                  "1.1",
                  "1",
                  "com.sksamuel.kotest.runner.junit5.FeatureSpecSample",
                  "Kotest"
               )
               finished().shouldHaveNames(
                  "1.1",
                  "1.2",
                  "1",
                  "2",
                  "com.sksamuel.kotest.runner.junit5.FeatureSpecSample",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "1",
                  "1.1",
                  "1.2",
                  "2"
               )
            }
      }
   }

   test("verify failure on zero assertion and strict assertion mode enable") {
      listOf(
         selectClass(FeatureSpecWithZeroAssertions::class.java),
         selectUniqueId(UniqueId.forEngine(EngineId).append(Segment.Spec.value, FeatureSpecWithZeroAssertions::class.qualifiedName))
      ).forAll { selector ->
         EngineTestKit
            .engine("kotest")
            .selectors(selector)
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               failed().shouldHaveNames("no assertion")
               succeeded().shouldHaveNames(
                  "one dummy assertion",
                  "assertion mode",
                  "com.sksamuel.kotest.runner.junit5.FeatureSpecWithZeroAssertions",
                  "Kotest"
               )
            }
      }
   }
})

private class FeatureSpecHappyPathSample : FeatureSpec() {
   init {
      feature("1") {
         scenario("1.1") {
         }
         feature("1.2") {
            scenario("1.2.1") {
            }
            feature("1.2.2") {
               scenario("1.2.2.1") {
               }
               scenario("1.2.2.2") {
                  1 shouldBe 2
               }
            }
         }
      }

      feature("2") {
         feature("2.1") {
            "a".shouldHaveLength(0)
            scenario("2.1.1") {
            }
         }
         scenario("2.1.2").config(enabledIf = { nonConstantFalse() }) {
         }
      }
   }
}


private class FeatureSpecSample : FeatureSpec() {
   init {

      val count = AtomicInteger(0)

      feature("1") {
         count.incrementAndGet().shouldBe(1)
         scenario("1.1") {
            count.incrementAndGet().shouldBe(2)
         }
         feature("1.2") {
            count.incrementAndGet().shouldBe(2)
            scenario("1.2.1") {
               count.incrementAndGet().shouldBe(3)
            }
            feature("1.2.2") {
               count.incrementAndGet().shouldBe(3)
               scenario("1.2.2.1") {
                  count.incrementAndGet().shouldBe(4)
               }
               scenario("1.2.2.2") {
                  count.incrementAndGet().shouldBe(4)
               }
            }
         }
      }

      feature("2") {
         count.incrementAndGet().shouldBe(1)
         feature("2.1") {
            count.incrementAndGet().shouldBe(2)
            scenario("2.1.1") {
               count.incrementAndGet().shouldBe(3)
            }
            scenario("2.1.2") {
               count.incrementAndGet().shouldBe(3)
            }
         }
      }
   }
}

private class FeatureSpecWithZeroAssertions : FeatureSpec() {
   init {

       feature("assertion mode") {
          scenario("no assertion") {}
          scenario("one dummy assertion") {
             1 shouldBe 1
          }
       }
   }

   override fun assertionMode() = AssertionMode.Error
}
