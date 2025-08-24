package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

@EnabledIf(LinuxOnlyGithubCondition::class)
class AnnotationExtensionTest : FunSpec() {
   init {

      beforeTest {
         instantiations = 0
         beforeSpec = 0
         afterSpec = 0
         afterTest = 0
         prepareSpec = 0
         beforeTest = 0
         beforeIntercept = 0
         afterIntercept = 0
      }

      test("a spec annotated with ApplyExtension should have that extension applied") {
         TestEngineLauncher().withListener(NoopTestEngineListener)
            .withClasses(MyAnnotatedSpec1::class)
            .launch()
         instantiations.shouldBe(1)
         beforeSpec.shouldBe(1)
         afterSpec.shouldBe(1)
         afterTest.shouldBe(1)
         beforeTest.shouldBe(1)
         beforeIntercept.shouldBe(1)
         afterIntercept.shouldBe(1)
         // prepareSpec.shouldBe(1)
      }

      test("a spec annotated with multiple ApplyExtension's should have all extensions applied") {
         TestEngineLauncher().withListener(NoopTestEngineListener)
            .withClasses(MyAnnotatedSpec2::class)
            .launch()
         instantiations.shouldBe(2)
         beforeSpec.shouldBe(2)
         afterSpec.shouldBe(2)
         afterTest.shouldBe(2)
         beforeTest.shouldBe(2)
         beforeIntercept.shouldBe(2)
         afterIntercept.shouldBe(2)
         // prepareSpec.shouldBe(2)
      }

      test("ApplyExtension should only apply to the spec they are annotating") {
         TestEngineLauncher().withListener(NoopTestEngineListener)
            .withClasses(MyAnnotatedSpec1::class, NotAnnotatedSpec::class)
            .launch()
         instantiations.shouldBe(1)
         beforeSpec.shouldBe(1)
         afterSpec.shouldBe(1)
         afterTest.shouldBe(1)
         beforeTest.shouldBe(1)
         beforeIntercept.shouldBe(1)
         afterIntercept.shouldBe(1)
         // prepareSpec.shouldBe(1)
      }
   }
}

@ApplyExtension(MyExtension::class)
private class MyAnnotatedSpec1 : FunSpec() {
   init {
      test("a") {}
   }
}

@ApplyExtension(MyExtension::class, MyExtension::class)
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

private var instantiations = 0
private var beforeSpec = 0
private var afterSpec = 0
private var beforeTest = 0
private var afterTest = 0
private var prepareSpec = 0
private var beforeIntercept = 0
private var afterIntercept = 0

class MyExtension : BeforeSpecListener,
   AfterSpecListener,
   BeforeTestListener,
   AfterTestListener,
   SpecExtension,
   PrepareSpecListener {

   init {
      instantiations++
   }

   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      beforeIntercept++
      execute(spec)
      afterIntercept++
   }

   override suspend fun beforeSpec(spec: Spec) {
      beforeSpec++
   }

   override suspend fun afterSpec(spec: Spec) {
      afterSpec++
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
