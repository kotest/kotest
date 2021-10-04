package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.ExtensionFactory
import io.kotest.core.extensions.SpecInitializeExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class AnnotationExtensionTest : FunSpec() {
   init {

      beforeTest {
         instantiations = 0
         beforeSpec = 0
         afterSpec = 0
         afterTest = 0
         prepareSpec = 0
         beforeTest = 0
         initializeSpec = 0
      }

      test("a spec annotated with ApplyExtension should have that extension applied") {
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(MyAnnotatedSpec1::class)
            .launch()
         instantiations.shouldBe(1)
         beforeSpec.shouldBe(1)
         afterSpec.shouldBe(1)
         afterTest.shouldBe(1)
         beforeTest.shouldBe(1)
         // prepareSpec.shouldBe(1)
         initializeSpec.shouldBe(1)
      }

      test("a spec annotated with multiple ApplyExtension's should have all extensions applied") {
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(MyAnnotatedSpec2::class)
            .launch()
         instantiations.shouldBe(2)
         beforeSpec.shouldBe(2)
         afterSpec.shouldBe(2)
         afterTest.shouldBe(2)
         beforeTest.shouldBe(2)
         // prepareSpec.shouldBe(2)
         initializeSpec.shouldBe(2)
      }

      test("ApplyExtension should only apply to the spec they are annotating") {
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(MyAnnotatedSpec1::class, NotAnnotatedSpec::class)
            .launch()
         instantiations.shouldBe(1)
         beforeSpec.shouldBe(1)
         afterSpec.shouldBe(1)
         afterTest.shouldBe(1)
         beforeTest.shouldBe(1)
         // prepareSpec.shouldBe(1)
         initializeSpec.shouldBe(1)
      }
   }
}

@ApplyExtension(MyExtensionFactory::class)
private class MyAnnotatedSpec1 : FunSpec() {
   init {
      test("a") {}
   }
}

@ApplyExtension(MyExtensionFactory::class, MyExtensionFactory::class)
private class MyAnnotatedSpec2 : FunSpec() {
   init {
      test("a") {}
   }
}

private class NotAnnotatedSpec : FunSpec() {
   init {
      test("a") {}
   }
}

object MyExtensionFactory : ExtensionFactory {
   override fun extension(spec: KClass<*>): Extension {
      return MyExtension()
   }
}

private var instantiations = 0
private var beforeSpec = 0
private var afterSpec = 0
private var beforeTest = 0
private var afterTest = 0
private var initializeSpec = 0
private var prepareSpec = 0

class MyExtension : BeforeSpecListener,
   AfterSpecListener,
   BeforeTestListener,
   AfterTestListener,
   SpecInitializeExtension,
   PrepareSpecListener {

   init {
      instantiations++
   }

   override suspend fun beforeSpec(spec: Spec) {
      beforeSpec++
   }

   override suspend fun afterSpec(spec: Spec) {
      afterSpec++
   }

   override suspend fun initializeSpec(spec: Spec): Spec {
      initializeSpec++
      return spec
   }

   override suspend fun beforeTest(testCase: TestCase) {
      beforeTest++
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afterTest++
   }

   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      prepareSpec++
   }
}
