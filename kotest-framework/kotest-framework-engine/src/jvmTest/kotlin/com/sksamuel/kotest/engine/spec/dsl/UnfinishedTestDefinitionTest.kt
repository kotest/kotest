package com.sksamuel.kotest.engine.spec.dsl

import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.TestDslState
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.string.shouldContain

@Isolate
class UnfinishedTestDefinitionTest : FunSpec() {
   init {

      afterEach {
         TestDslState.reset()
      }

      test("fun spec") {
         val result = KotestEngineLauncher()
            .withListener(NoopTestEngineListener)
            .withSpec(FunSpecUnfinishedTestDefinitionTest::class)
            .launch()
         result.errors.forAtLeastOne { it.message!!.shouldContain("unfinished test") }
      }

      test("describe spec") {
         val result = KotestEngineLauncher()
            .withListener(NoopTestEngineListener)
            .withSpec(DescribeSpecUnfinishedTestDefinitionTest::class)
            .launch()
         result.errors.forAtLeastOne { it.message!!.shouldContain("unfinished it") }
      }

      test("should spec") {
         val result = KotestEngineLauncher()
            .withListener(NoopTestEngineListener)
            .withSpec(ShouldSpecUnfinishedTestDefinitionTest::class)
            .launch()
         result.errors.forAtLeastOne { it.message!!.shouldContain("unfinished should") }
      }

      test("feature spec") {
         val result = KotestEngineLauncher()
            .withListener(NoopTestEngineListener)
            .withSpec(FeatureSpecUnfinishedTestDefinitionTest::class)
            .launch()
         result.errors.forAtLeastOne { it.message!!.shouldContain("unfinished scenario") }
      }

      test("expect spec") {
         val result = KotestEngineLauncher()
            .withListener(NoopTestEngineListener)
            .withSpec(ExpectSpecUnfinishedTestDefinitionTest::class)
            .launch()
         result.errors.forAtLeastOne { it.message!!.shouldContain("unfinished expect") }
      }
   }
}
