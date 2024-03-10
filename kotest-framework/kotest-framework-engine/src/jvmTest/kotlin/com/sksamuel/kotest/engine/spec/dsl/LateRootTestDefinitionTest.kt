package com.sksamuel.kotest.engine.spec.dsl

import io.kotest.core.annotation.Description
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.names.TestName
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
class LateRootTestDefinitionTest : FunSpec() {
   init {

      test("expect spec") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(ExpectSpecWithExtraRootTests::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         listener.result("foo")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }

      test("feature spec") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(FeatureSpecWithExtraRootTests::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         listener.result("foo")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }

      test("free spec") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(FreeSpecWithExtraRootTests::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         listener.result("foo")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }

      test("fun spec") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(FunSpecWithExtraRootTests::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         listener.result("foo")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }

      test("should spec") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(ShouldSpecWithExtraRootTests::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         listener.result("foo")!!.errorOrNull!!.shouldBeInstanceOf<InvalidDslException>()
      }
   }
}

private class FreeSpecWithExtraRootTests : FreeSpec() {
   init {
      "foo" {
         this@FreeSpecWithExtraRootTests.addTest(TestName("bar"), false, null) { }
      }
   }
}


private class FunSpecWithExtraRootTests : FunSpec() {
   init {
      test("foo") {
         this@FunSpecWithExtraRootTests.addTest(TestName("bar"), false, null) { }
      }
   }
}

private class ShouldSpecWithExtraRootTests : ShouldSpec() {
   init {
      should("foo") {
         this@ShouldSpecWithExtraRootTests.addTest(TestName("bar"), false, null) { }
      }
   }
}


private class ExpectSpecWithExtraRootTests : ExpectSpec() {
   init {
      context("foo") {
         this@ExpectSpecWithExtraRootTests.addTest(TestName("bar"), false, null) { }
      }
   }
}

private class FeatureSpecWithExtraRootTests : FeatureSpec() {
   init {
      feature("foo") {
         this@FeatureSpecWithExtraRootTests.addTest(TestName("bar"), false, null) { }
      }
   }
}
