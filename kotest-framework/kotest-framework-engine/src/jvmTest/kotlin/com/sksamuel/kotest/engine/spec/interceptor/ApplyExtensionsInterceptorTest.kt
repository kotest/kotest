package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.DefaultExtensionRegistry
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.ref.ApplyExtensionsInterceptor
import io.kotest.engine.test.TestResult
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class ApplyExtensionsInterceptorTest : FunSpec() {
   init {

      test("ApplyExtensionsInterceptor should apply extensions") {
         val registry = DefaultExtensionRegistry()
         ApplyExtensionsInterceptor(NoopTestEngineListener, registry)
            .intercept(SpecRef.Reference(MyAnnotatedSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  registry.all().single().shouldBeInstanceOf<Foo>()
                  return Result.success(emptyMap())
               }
            })
      }

      test("ApplyExtensionsInterceptor should apply extensions where the primary constructor is not no-args") {
         val registry = DefaultExtensionRegistry()
         ApplyExtensionsInterceptor(NoopTestEngineListener, registry)
            .intercept(SpecRef.Reference(MyAnnotatedSpec2::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  registry.all().single().shouldBeInstanceOf<Bar>()
                  return Result.success(emptyMap())
               }
            })
      }

      test("ApplyExtensionsInterceptor should apply extensions where the referenced extension is an object") {

         val collector = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(collector)
            .withClasses(MyAnnotatedSpec3::class)
            .launch()

         // if apply extension was not applied, it would fail to intercept the failing test
         collector.tests.keys.single().name.name shouldBe "foo"
         collector.tests.values.single().isSuccess shouldBe true
      }

      test("ApplyExtensions should error for a private object") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(collector)
            .withClasses(MyAnnotatedSpec4::class)
            .launch()
         collector.specs.toList()
            .first().second.errorOrNull.shouldNotBeNull().message shouldContain "Cannot use private class"
      }
   }
}

class Foo : Extension

class Bar(private val name: String) : Extension {
   constructor() : this("bar")
}

class Baz : TestCaseExtension {
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      return TestResult.Success(0.seconds)
   }
}

private object PrivateTestExtension : TestCaseExtension {
   var counter = 0
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      counter++
      return execute(testCase)
   }
}

@ApplyExtension(Foo::class)
private class MyAnnotatedSpec : FunSpec()

@ApplyExtension(Bar::class)
private class MyAnnotatedSpec2 : FunSpec()

@ApplyExtension(Baz::class)
private class MyAnnotatedSpec3 : FunSpec() {
   init {
      test("foo") { error("boom") }
   }
}

@ApplyExtension(PrivateTestExtension::class)
private class MyAnnotatedSpec4 : FunSpec() {
   init {
      test("foo") { }
   }
}
