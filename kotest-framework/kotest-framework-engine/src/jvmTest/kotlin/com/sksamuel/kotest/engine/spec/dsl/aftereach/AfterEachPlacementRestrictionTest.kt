package com.sksamuel.kotest.engine.spec.dsl.aftereach

import io.kotest.core.annotation.Description
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.InvalidDslException
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeInstanceOf

@Description("Tests that afterEach cannot be defined after a test")
@EnabledIf(LinuxOnlyGithubCondition::class)
class AfterEachPlacementRestrictionTest : FunSpec() {
   init {

      test("FunSpec") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(FunSpecWithAfterEach::class)
            .launch()
         listener.result("foo1")!!.isSuccess.shouldBeTrue()
         listener.result("foo2")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }

      test("ShouldSpec") {

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(ShouldSpecWithAfterEach::class)
            .launch()
         listener.result("foo1")!!.isSuccess.shouldBeTrue()
         listener.result("foo2")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }

      test("ExpectSpec") {

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(ExpectSpecWithAfterEach::class)
            .launch()
         listener.result("foo1")!!.isSuccess.shouldBeTrue()
         listener.result("foo2")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }

      test("WordSpec") {

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(WordSpecWithAfterEach::class)
            .launch()
         listener.result("foo1")!!.isSuccess.shouldBeTrue()
         listener.result("foo2")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }

      test("FreeSpec") {

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(FreeSpecWithAfterEach::class)
            .launch()
         listener.result("foo1")!!.isSuccess.shouldBeTrue()
         listener.result("foo2")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }
   }
}

private class FunSpecWithAfterEach : FunSpec() {
   init {
      context("foo1") {
         afterEach {}
         test("bar1") {}
      }
      context("foo2") {
         test("bar2") {}
         afterEach {}
      }
   }
}

private class ShouldSpecWithAfterEach : ShouldSpec() {
   init {
      context("foo1") {
         afterEach {}
         should("bar1") {}
      }
      context("foo2") {
         should("bar2") {}
         afterEach {}
      }
   }
}

private class ExpectSpecWithAfterEach : ExpectSpec() {
   init {
      context("foo1") {
         afterEach {}
         expect("bar1") {}
      }
      context("foo2") {
         expect("bar2") {}
         afterEach {}
      }
   }
}

private class WordSpecWithAfterEach : WordSpec() {
   init {
      "foo1" should {
         afterEach {}
         "bar1" {}
      }
      "foo2" should {
         "bar2" {}
         afterEach {}
      }
   }
}

private class FreeSpecWithAfterEach : FreeSpec() {
   init {
      "foo1" - {
         afterEach {}
         "bar1" {}
      }
      "foo2" - {
         "bar2" {}
         afterEach {}
      }
   }
}
