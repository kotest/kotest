package com.sksamuel.kotest.engine.spec.dsl

import io.kotest.core.annotation.Description
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.InvalidDslException
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.scopes.addTest
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.types.shouldBeInstanceOf

@Description("Tests that a spec cannot define root tests after the spec has been instantiated")
@EnabledIf(LinuxOnlyGithubCondition::class)
class LateRootTestDefinitionTest : FunSpec() {
   init {

      test("expect spec") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher().withListener(listener)
            .withClasses(ExpectSpecWithExtraRootTests::class)
            .launch()
         listener.result("foo")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }

      test("feature spec") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher().withListener(listener)
            .withClasses(FeatureSpecWithExtraRootTests::class)
            .launch()
         listener.result("foo")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }

      test("free spec") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher().withListener(listener)
            .withClasses(FreeSpecWithExtraRootTests::class)
            .launch()
         listener.result("foo")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }

      test("fun spec") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher().withListener(listener)
            .withClasses(FunSpecWithExtraRootTests::class)
            .launch()
         listener.result("foo")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }

      test("should spec") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher().withListener(listener)
            .withClasses(ShouldSpecWithExtraRootTests::class)
            .launch()
         listener.result("foo")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }
   }
}

private class FreeSpecWithExtraRootTests : FreeSpec() {
   init {
      "foo" {
         this@FreeSpecWithExtraRootTests.addTest(TestNameBuilder.builder("bar").build(), false, false, null) { }
      }
   }
}


private class FunSpecWithExtraRootTests : FunSpec() {
   init {
      test("foo") {
         this@FunSpecWithExtraRootTests.addTest(TestNameBuilder.builder("bar").build(), false, false, null) { }
      }
   }
}

private class ShouldSpecWithExtraRootTests : ShouldSpec() {
   init {
      should("foo") {
         this@ShouldSpecWithExtraRootTests.addTest(TestNameBuilder.builder("bar").build(), false, false, null) { }
      }
   }
}


private class ExpectSpecWithExtraRootTests : ExpectSpec() {
   init {
      context("foo") {
         this@ExpectSpecWithExtraRootTests.addTest(TestNameBuilder.builder("bar").build(), false, false, null) { }
      }
   }
}

private class FeatureSpecWithExtraRootTests : FeatureSpec() {
   init {
      feature("foo") {
         this@FeatureSpecWithExtraRootTests.addTest(TestNameBuilder.builder("bar").build(), false, false, null) { }
      }
   }
}
