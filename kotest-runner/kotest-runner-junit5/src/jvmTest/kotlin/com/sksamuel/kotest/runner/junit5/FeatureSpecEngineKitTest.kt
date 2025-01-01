package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.AssertionMode
import io.kotest.inspectors.forAll
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine.Companion.ENGINE_ID
import io.kotest.runner.junit.platform.Segment
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.engine.discovery.DiscoverySelectors.selectUniqueId
import org.junit.platform.testkit.engine.EngineTestKit
import java.util.concurrent.atomic.AtomicInteger

@EnabledIf(LinuxCondition::class)
class FeatureSpecEngineKitTest : FunSpec({

   test("verify engine events happy path") {
      listOf(
         selectClass(FeatureSpecHappyPathSample::class.java),
         selectUniqueId(
            UniqueId.forEngine(ENGINE_ID).append(Segment.Spec.value, FeatureSpecHappyPathSample::class.qualifiedName)
         )
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
                  "a", "b", "c", "d", "e", "f", "g", "h", "i",
               )
               aborted().shouldBeEmpty()
               skipped().shouldHaveNames("k")
               failed().shouldHaveNames(
                  "g", "i",
               )
               succeeded().shouldHaveNames(
                  "b", "d", "f", "e", "c", "a", "h",
                  "com.sksamuel.kotest.runner.junit5.FeatureSpecHappyPathSample",
                  "Kotest"
               )
               finished().shouldHaveNames(
                  "b", "d", "f", "g", "e", "c", "a", "i", "h",
                  "com.sksamuel.kotest.runner.junit5.FeatureSpecHappyPathSample",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "a", "b", "c", "d", "e", "f", "g", "h", "i", "k"
               )
            }
      }
   }

   test("verify engine events all errors path") {
      listOf(
         selectClass(FeatureSpecSample::class.java),
         selectUniqueId(UniqueId.forEngine(ENGINE_ID).append(Segment.Spec.value, FeatureSpecSample::class.qualifiedName))
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
                  "a", "b", "c", "h",
               )
               aborted().shouldBeEmpty()
               skipped().shouldHaveNames()
               failed().shouldHaveNames(
                  "c", "h"
               )
               succeeded().shouldHaveNames(
                  "b",
                  "a",
                  "com.sksamuel.kotest.runner.junit5.FeatureSpecSample",
                  "Kotest"
               )
               finished().shouldHaveNames(
                  "b",
                  "c",
                  "a",
                  "h",
                  "com.sksamuel.kotest.runner.junit5.FeatureSpecSample",
                  "Kotest"
               )
               dynamicallyRegistered().shouldHaveNames(
                  "a", "b", "c", "h"
               )
            }
      }
   }

   test("verify failure on zero assertion and strict assertion mode enable") {
      listOf(
         selectClass(FeatureSpecWithZeroAssertions::class.java),
         selectUniqueId(
            UniqueId.forEngine(ENGINE_ID).append(Segment.Spec.value, FeatureSpecWithZeroAssertions::class.qualifiedName)
         )
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
      feature("a") {
         scenario("b") {
         }
         feature("c") {
            scenario("d") {
            }
            feature("e") {
               scenario("f") {
               }
               scenario("g") {
                  1 shouldBe 2
               }
            }
         }
      }

      feature("h") {
         feature("i") {
            "a".shouldHaveLength(0)
            scenario("j") {
            }
         }
         scenario("k").config(enabledIf = { nonConstantFalse() }) {
         }
      }
   }
}

/** returns `false` while preventing the compiler from optimizing it away. */
fun nonConstantFalse() = System.currentTimeMillis() == 0L

private class FeatureSpecSample : FeatureSpec() {
   init {

      val count = AtomicInteger(0)

      feature("a") {
         count.incrementAndGet().shouldBe(1)
         scenario("b") {
            count.incrementAndGet().shouldBe(2)
         }
         feature("c") {
            count.incrementAndGet().shouldBe(2)
            scenario("d") {
               count.incrementAndGet().shouldBe(3)
            }
            feature("e") {
               count.incrementAndGet().shouldBe(3)
               scenario("f") {
                  count.incrementAndGet().shouldBe(4)
               }
               scenario("g") {
                  count.incrementAndGet().shouldBe(4)
               }
            }
         }
      }

      feature("h") {
         count.incrementAndGet().shouldBe(1)
         feature("i") {
            count.incrementAndGet().shouldBe(2)
            scenario("j") {
               count.incrementAndGet().shouldBe(3)
            }
            scenario("k") {
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
