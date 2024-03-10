package com.sksamuel.kotest.engine.spec.dsl

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.scopes.TestDslState
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.string.shouldContain

class UnfinishedTestDefinitionTest : FunSpec() {
   init {

      afterEach {
         TestDslState.reset()
      }

      test("fun spec") {
         val result = TestEngineLauncher(NoopTestEngineListener)
            .withClasses(FunSpecUnfinishedTestDefinitionTest::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         result.errors.forAtLeastOne { it.message!!.shouldContain("unfinished test") }
      }

      test("describe spec") {
         val result = TestEngineLauncher(NoopTestEngineListener)
            .withClasses(DescribeSpecUnfinishedTestDefinitionTest::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         result.errors.forAtLeastOne { it.message!!.shouldContain("unfinished it") }
      }

      test("should spec") {
         val result = TestEngineLauncher(NoopTestEngineListener)
            .withClasses(ShouldSpecUnfinishedTestDefinitionTest::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         result.errors.forAtLeastOne { it.message!!.shouldContain("unfinished should") }
      }

      test("feature spec") {
         val result = TestEngineLauncher(NoopTestEngineListener)
            .withClasses(FeatureSpecUnfinishedTestDefinitionTest::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         result.errors.forAtLeastOne { it.message!!.shouldContain("unfinished scenario") }
      }

      test("expect spec") {
         val result = TestEngineLauncher(NoopTestEngineListener)
            .withClasses(ExpectSpecUnfinishedTestDefinitionTest::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         result.errors.forAtLeastOne { it.message!!.shouldContain("unfinished expect") }
      }
   }
}

private class FunSpecUnfinishedTestDefinitionTest : FunSpec({
   context("context") {
      test("unfinished test")
   }
})

private class FeatureSpecUnfinishedTestDefinitionTest : FeatureSpec({
   feature("feature") {
      scenario("unfinished scenario")
   }
})

private class ShouldSpecUnfinishedTestDefinitionTest : ShouldSpec({

   context("context") {
      should("unfinished should")
   }
})

private class ExpectSpecUnfinishedTestDefinitionTest : ExpectSpec({

   context("context") {
      expect("unfinished expect")
   }
})

private class DescribeSpecUnfinishedTestDefinitionTest : DescribeSpec({

   describe("describe") {
      it("unfinished it")
   }

})
