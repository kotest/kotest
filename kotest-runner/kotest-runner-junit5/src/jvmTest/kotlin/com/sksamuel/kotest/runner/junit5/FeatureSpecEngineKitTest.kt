package com.sksamuel.kotest.runner.junit5

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.shouldBe
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit
import java.util.concurrent.atomic.AtomicInteger

class FeatureSpecEngineKitTest : FunSpec({

   test("verify engine events happy path") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(FeatureSpecHappyPathSample::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.FeatureSpecHappyPathSample",
               "Feature: 1",
               "Scenario: 1.1",
               "Feature: 1.2",
               "Scenario: 1.2.1",
               "Feature: 1.2.2",
               "Scenario: 1.2.2.1",
               "Scenario: 1.2.2.2",
               "Feature: 2",
               "Feature: 2.1"
            )
            aborted().shouldBeEmpty()
            skipped().shouldHaveNames("Scenario: 2.1.2")
            failed().shouldHaveNames(
               "Scenario: 1.2.2.2",
               "Feature: 1.2.2",
               "Feature: 1.2",
               "Feature: 1",
               "Feature: 2.1",
               "Feature: 2",
               "com.sksamuel.kotest.runner.junit5.FeatureSpecHappyPathSample"
            )
            succeeded().shouldHaveNames(
               "Scenario: 1.1", "Scenario: 1.2.1", "Scenario: 1.2.2.1", "Kotest"
            )
            finished().shouldHaveNames(
               "Scenario: 1.1",
               "Scenario: 1.2.1",
               "Scenario: 1.2.2.1",
               "Scenario: 1.2.2.2",
               "Feature: 1.2.2",
               "Feature: 1.2",
               "Feature: 1",
               "Feature: 2.1",
               "Feature: 2",
               "com.sksamuel.kotest.runner.junit5.FeatureSpecHappyPathSample",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.runner.junit5.FeatureSpecHappyPathSample",
               "Feature: 1",
               "Scenario: 1.1",
               "Feature: 1.2",
               "Scenario: 1.2.1",
               "Feature: 1.2.2",
               "Scenario: 1.2.2.1",
               "Scenario: 1.2.2.2",
               "Feature: 2",
               "Feature: 2.1",
               "Scenario: 2.1.2"
            )
         }
   }

   test("verify engine events all errors path") {
      EngineTestKit
         .engine("kotest")
         .selectors(selectClass(FeatureSpecSample::class.java))
         .configurationParameter("allow_private", "true")
         .execute()
         .allEvents().apply {
            started().shouldHaveNames(
               "Kotest",
               "com.sksamuel.kotest.runner.junit5.FeatureSpecSample",
               "Feature: 1",
               "Scenario: 1.1",
               "Feature: 1.2",
               "Feature: 2"
            )
            aborted().shouldBeEmpty()
            skipped().shouldHaveNames()
            failed().shouldHaveNames(
               "Feature: 1.2", "Feature: 1", "Feature: 2", "com.sksamuel.kotest.runner.junit5.FeatureSpecSample"
            )
            succeeded().shouldHaveNames(
               "Scenario: 1.1", "Kotest"
            )
            finished().shouldHaveNames(
               "Scenario: 1.1",
               "Feature: 1.2",
               "Feature: 1",
               "Feature: 2",
               "com.sksamuel.kotest.runner.junit5.FeatureSpecSample",
               "Kotest"
            )
            dynamicallyRegistered().shouldHaveNames(
               "com.sksamuel.kotest.runner.junit5.FeatureSpecSample",
               "Feature: 1",
               "Scenario: 1.1",
               "Feature: 1.2",
               "Feature: 2"
            )
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
         scenario("2.1.2").config(enabledIf = { System.currentTimeMillis() == 0L }) {
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
