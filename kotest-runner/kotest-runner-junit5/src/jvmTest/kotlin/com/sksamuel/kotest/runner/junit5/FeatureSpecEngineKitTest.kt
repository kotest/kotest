package com.sksamuel.kotest.runner.junit5

import io.kotest.common.nonConstantFalse
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.AssertionMode
import io.kotest.inspectors.forAll
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import io.kotest.runner.junit.platform.Segment
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.engine.discovery.DiscoverySelectors.selectUniqueId
import org.junit.platform.testkit.engine.EngineTestKit
import java.util.concurrent.atomic.AtomicInteger

@EnabledIf(NotMacOnGithubCondition::class)
class FeatureSpecEngineKitTest : FunSpec({

   test("verify engine events happy path") {
      listOf(
         selectClass(FeatureSpecHappyPathSample::class.java),
         selectUniqueId(
            UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID)
               .append(Segment.Spec.value, FeatureSpecHappyPathSample::class.qualifiedName)
         )
      ).forAll { selector ->
         EngineTestKit
            .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
            .selectors(selector)
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               started().shouldHaveNames(
                  KotestJunitPlatformTestEngine.ENGINE_NAME,
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
                  KotestJunitPlatformTestEngine.ENGINE_NAME,
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
         selectUniqueId(
            UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID)
               .append(Segment.Spec.value, FeatureSpecSample::class.qualifiedName)
         )
      ).forAll { selector ->
         EngineTestKit
            .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
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
                  KotestJunitPlatformTestEngine.ENGINE_NAME,
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
            UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID)
               .append(Segment.Spec.value, FeatureSpecWithZeroAssertions::class.qualifiedName)
         )
      ).forAll { selector ->
         EngineTestKit
            .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
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


private class FeatureSpecSample : FeatureSpec() {
   init {

      val count = AtomicInteger(0)

      feature("a") {
         count.incrementAndGet().shouldBe(1)
         scenario("b") {
            count.incrementAndGet().shouldBe(2)
         }
         feature("c") {
            count.incrementAndGet().shouldBe(111111)
            scenario("d") {
               count.incrementAndGet().shouldBe(4)
            }
            feature("e") {
               count.incrementAndGet().shouldBe(5)
               scenario("f") {
                  count.incrementAndGet().shouldBe(6)
               }
               scenario("g") {
                  count.incrementAndGet().shouldBe(7)
               }
            }
         }
      }

      feature("h") {
         count.incrementAndGet().shouldBe(11111)
         feature("i") {
            count.incrementAndGet().shouldBe(9)
            scenario("j") {
               count.incrementAndGet().shouldBe(10)
            }
            scenario("k") {
               count.incrementAndGet().shouldBe(11)
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
