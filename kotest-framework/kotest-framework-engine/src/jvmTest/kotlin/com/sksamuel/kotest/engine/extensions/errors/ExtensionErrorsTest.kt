package com.sksamuel.kotest.engine.extensions.errors

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf

class ExtensionErrorsTest : FunSpec() {
   init {
      test("beforeSpec function overrides should be wrapped") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(AfterSpecFunctionOverrideError::class)
            .launch()
         val error = collector.specs.values.first()
         error.shouldBeInstanceOf<ExtensionException.AfterSpecException>()
      }

      test("beforeSpec DSL errors should be wrapped") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(AfterSpecDSLError::class)
            .launch()
         val error = collector.specs.values.first()
         error.shouldBeInstanceOf<ExtensionException.AfterSpecException>()
      }

      test("multiple beforeSpec should be collected") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MultipleAfterSpecErrors::class)
            .launch()
         val error = collector.specs.values.first()
         error.shouldBeInstanceOf<MultipleExceptions>()
         error.causes.shouldHaveSize(3)
         error.causes.forAll { it.shouldBeInstanceOf<ExtensionException.AfterSpecException>() }
      }

      test("afterSpec function overrides should be wrapped") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(AfterSpecFunctionOverrideError::class)
            .launch()
         val error = collector.specs.values.first()
         error.shouldBeInstanceOf<ExtensionException.AfterSpecException>()
      }

      test("afterSpec DSL errors should be wrapped") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(AfterSpecDSLError::class)
            .launch()
         val error = collector.specs.values.first()
         error.shouldBeInstanceOf<ExtensionException.AfterSpecException>()
      }

      test("multiple afterSpec should be collected") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MultipleAfterSpecErrors::class)
            .launch()
         val error = collector.specs.values.first()
         error.shouldBeInstanceOf<MultipleExceptions>()
         error.causes.shouldHaveSize(3)
         error.causes.forAll { it.shouldBeInstanceOf<ExtensionException.AfterSpecException>() }
      }
   }
}

private class BeforeSpecFunctionOverrideError : FunSpec() {
   override fun beforeSpec(spec: Spec) {
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

   override fun beforeSpec(spec: Spec) {
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
   override fun afterSpec(spec: Spec) {
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
   override fun afterSpec(spec: Spec) {
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
