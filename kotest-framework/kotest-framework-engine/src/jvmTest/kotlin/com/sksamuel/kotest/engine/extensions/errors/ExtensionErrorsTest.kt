package com.sksamuel.kotest.engine.extensions.errors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf

@EnabledIf(LinuxOnlyGithubCondition::class)
class ExtensionErrorsTest : FunSpec() {
   init {
      test("beforeSpec function overrides should be wrapped") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(BeforeSpecFunctionOverrideError::class)
            .launch()
         val error = collector.specs.values.first().errorOrNull
         error.shouldBeInstanceOf<ExtensionException.BeforeSpecException>()
      }

      test("beforeSpec DSL errors should be wrapped") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(BeforeSpecDSLError::class)
            .launch()
         val error = collector.specs.values.first().errorOrNull
         error.shouldBeInstanceOf<ExtensionException.BeforeSpecException>()
      }

      test("multiple beforeSpec should be collected") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MultipleBeforeSpecErrors::class)
            .launch()
         val error = collector.specs.values.first().errorOrNull
         error.shouldBeInstanceOf<MultipleExceptions>()
         error.causes.shouldHaveSize(3)
         error.causes.forAll { it.shouldBeInstanceOf<ExtensionException.BeforeSpecException>() }
      }

      test("afterSpec function overrides should be wrapped") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(AfterSpecFunctionOverrideError::class)
            .launch()
         val error = collector.specs.values.first().errorOrNull
         error.shouldBeInstanceOf<ExtensionException.AfterSpecException>()
      }

      test("afterSpec DSL errors should be wrapped") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(AfterSpecDSLError::class)
            .launch()
         val error = collector.specs.values.first().errorOrNull
         error.shouldBeInstanceOf<ExtensionException.AfterSpecException>()
      }

      test("multiple afterSpec should be collected") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MultipleAfterSpecErrors::class)
            .launch()
         val error = collector.specs.values.first().errorOrNull
         error.shouldBeInstanceOf<MultipleExceptions>()
         error.causes.shouldHaveSize(3)
         error.causes.forAll { it.shouldBeInstanceOf<ExtensionException.AfterSpecException>() }
      }

      test("beforeTest function overrides should be wrapped") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(BeforeTestFunctionOverrideError::class)
            .launch()
         val error = collector.tests.values.first().errorOrNull
         error.shouldBeInstanceOf<ExtensionException.BeforeAnyException>()
      }

      test("beforeTest DSL errors should be wrapped") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(BeforeTestDSLError::class)
            .launch()
         val error = collector.tests.values.first().errorOrNull
         error.shouldBeInstanceOf<ExtensionException.BeforeAnyException>()
      }

      test("multiple beforeTest errors should be collected") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MultipleBeforeTestErrors::class)
            .launch()
         val error = collector.tests.values.first().errorOrNull
         error.shouldBeInstanceOf<MultipleExceptions>()
         error.causes.shouldHaveSize(3)
         error.causes.forAll { it.shouldBeInstanceOf<ExtensionException.BeforeAnyException>() }
      }

      test("afterTest function overrides should be wrapped") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(AfterTestFunctionOverrideError::class)
            .launch()
         val error = collector.tests.values.first().errorOrNull
         error.shouldBeInstanceOf<ExtensionException.AfterAnyException>()
      }

      test("afterTest DSL errors should be wrapped") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(AfterTestDSLError::class)
            .launch()
         val error = collector.tests.values.first().errorOrNull
         error.shouldBeInstanceOf<ExtensionException.AfterAnyException>()
      }

      test("multiple afterTest errors should be collected") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MultipleAfterTestErrors::class)
            .launch()
         val error = collector.tests.values.first().errorOrNull
         error.shouldBeInstanceOf<MultipleExceptions>()
         error.causes.shouldHaveSize(3)
         error.causes.forAll { it.shouldBeInstanceOf<ExtensionException.AfterAnyException>() }
      }
   }
}

private class BeforeSpecFunctionOverrideError : FunSpec() {
   override suspend fun beforeSpec(spec: Spec) {
      error("foo")
   }

   init {
      test("a") { }
   }
}

private class BeforeSpecDSLError : FunSpec() {
   init {
      beforeSpec {
         error("foo")
      }
      test("a") { }
   }
}

private class MultipleBeforeSpecErrors : FunSpec() {

   override suspend fun beforeSpec(spec: Spec) {
      error("foo")
   }

   init {
      beforeSpec {
         error("bar")
      }
      beforeSpec {
         error("baz")
      }
      test("a") { }
   }
}


private class AfterSpecFunctionOverrideError : FunSpec() {
   override suspend fun afterSpec(spec: Spec) {
      error("foo")
   }

   init {
      test("a") { }
   }
}

private class AfterSpecDSLError : FunSpec() {
   init {
      afterSpec {
         error("foo")
      }
      test("a") { }
   }
}

private class MultipleAfterSpecErrors : FunSpec() {
   override suspend fun afterSpec(spec: Spec) {
      error("foo")
   }

   init {
      afterSpec {
         error("bar")
      }
      afterSpec {
         error("baz")
      }
      test("a") { }
   }
}

private class BeforeTestFunctionOverrideError : FunSpec() {

   override suspend fun beforeTest(testCase: TestCase) {
      error("foo")
   }

   init {
      test("a") { }
   }
}

private class BeforeTestDSLError : FunSpec() {
   init {
      beforeTest {
         error("foo")
      }
      test("a") { }
   }
}

private class MultipleBeforeTestErrors : FunSpec() {

   override suspend fun beforeTest(testCase: TestCase) {
      error("foo")
   }

   init {
      beforeTest {
         error("bar")
      }
      beforeTest {
         error("baz")
      }
      test("a") { }
   }
}


private class AfterTestFunctionOverrideError : FunSpec() {
   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      error("foo")
   }

   init {
      test("a") { }
   }
}

private class AfterTestDSLError : FunSpec() {
   init {
      afterTest {
         error("foo")
      }
      test("a") { }
   }
}

private class MultipleAfterTestErrors : FunSpec() {
   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      error("foo")
   }

   init {
      afterTest {
         error("bar")
      }
      afterTest {
         error("baz")
      }
      test("a") { }
   }
}

