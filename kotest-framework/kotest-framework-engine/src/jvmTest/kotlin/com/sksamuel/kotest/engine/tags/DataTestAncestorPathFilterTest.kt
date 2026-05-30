package com.sksamuel.kotest.engine.tags

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.tags.TagExpression
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe

/**
 * Tests that [KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH] causes only the direct
 * ancestor containers of a data test to bypass tag filtering, while sibling containers at any
 * level remain correctly excluded.
 *
 * This is the engine-side complement to the IntelliJ plugin's data test run configuration:
 * when a user clicks to run a specific data test nested inside regular containers, the plugin
 * sets [KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH] to the path of the enclosing
 * regular containers (e.g. "parent context -- child context") so the engine knows exactly
 * which containers to allow through, without accidentally running sibling containers.
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
@Isolate
class DataTestAncestorPathFilterTest : FunSpec({

   test("ancestor containers bypass tag filter while sibling containers are excluded") {
      try {
         System.setProperty(
            KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH,
            "parent context -- child context"
         )
         val listener = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(listener)
            .withSpecRefs(SpecRef.Reference(DataTestWithSiblingContainerSpec::class))
            .withoutEnvFilters()
            .withTagExpression(TagExpression("kotest.data"))
            .execute()

         val results = listener.tests

         // Direct ancestor containers should run (not ignored)
         results.entries.first { it.key.name.name == "parent context" }.value.isIgnored shouldBe false
         results.entries.first { it.key.name.name == "child context" }.value.isIgnored shouldBe false

         // The data test items should run
         results.entries.first { it.key.name.name == "data1" }.value.isIgnored shouldBe false
         results.entries.first { it.key.name.name == "data2" }.value.isIgnored shouldBe false

         // The sibling container and its test should be excluded
         results.entries.first { it.key.name.name == "sibling container" }.value.isIgnored shouldBe true
      } finally {
         System.getProperties().remove(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH)
      }
   }

   test("only the matching ancestor path is allowed through, not deeper or unrelated containers") {
      try {
         // ancestorPath is "parent context" only - so "child context" should NOT be bypassed
         System.setProperty(
            KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH,
            "parent context"
         )
         val listener = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(listener)
            .withSpecRefs(SpecRef.Reference(DataTestWithSiblingContainerSpec::class))
            .withoutEnvFilters()
            .withTagExpression(TagExpression("kotest.data"))
            .execute()

         val results = listener.tests

         // "parent context" path matches exactly → allowed through
         results.entries.first { it.key.name.name == "parent context" }.value.isIgnored shouldBe false

         // "child context" path is "parent context -- child context", not a prefix of "parent context" → excluded
         results.entries.first { it.key.name.name == "child context" }.value.isIgnored shouldBe true
      } finally {
         System.getProperties().remove(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH)
      }
   }

   test("without KOTEST_DATA_TEST_ANCESTOR_PATH the data tests are not reachable") {
      val listener = CollectingTestEngineListener()
      TestEngineLauncher()
         .withListener(listener)
         .withSpecRefs(SpecRef.Reference(DataTestWithSiblingContainerSpec::class))
         .withoutEnvFilters()
         .withTagExpression(TagExpression("kotest.data"))
         .execute()

      val results = listener.tests

      // Without the ancestor path, the engine cannot traverse into the ancestor containers
      // to discover the data tests — so "data1" and "data2" never run at all
      results.keys.none { it.name.name == "data1" } shouldBe true
      results.keys.none { it.name.name == "data2" } shouldBe true
   }
})

private class DataTestWithSiblingContainerSpec : DescribeSpec({
   context("parent context") {
      context("child context") {
         withData("data1", "data2") { }
         describe("sibling container") {
            it("test inside sibling") { }
         }
      }
   }
})
