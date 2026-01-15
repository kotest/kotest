package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.test.TestResult
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class ApplyExtensionsTest : FunSpec() {
   init {

      test("@ApplyExtensions should apply extensions for a class") {

         val collector = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(collector)
            .withClasses(MyAnnotatedSpec1::class)
            .execute()

         // if apply extension was not applied, it would fail to intercept the failing test
         collector.tests.keys.single().name.name shouldBe "foo"
         collector.tests.values.single().isSuccess shouldBe true
      }

      test("@ApplyExtensions should apply extensions for a class using secondary constructor if no args") {

         val collector = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(collector)
            .withClasses(MyAnnotatedSpec2::class)
            .execute()

         // if apply extension was not applied, it would fail to intercept the failing test
         collector.tests.keys.single().name.name shouldBe "foo"
         collector.tests.values.single().isSuccess shouldBe true
      }

      test("@ApplyExtensions should apply extensions where the referenced extension is an object") {

         val collector = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(collector)
            .withClasses(MyAnnotatedSpec3::class)
            .execute()

         // if apply extension was not applied, it would fail to intercept the failing test
         collector.tests.keys.single().name.name shouldBe "foo"
         collector.tests.values.single().isSuccess shouldBe true
      }

      test("@ApplyExtensions should error for a private object") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(collector)
            .withClasses(MyAnnotatedSpec4::class)
            .execute()
         collector.specs.toList()
            .first().second.errorOrNull.shouldNotBeNull().message shouldContain "Cannot use private class"
      }
   }
}

class Foo : TestCaseExtension {
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      return TestResult.Success(0.seconds)
   }
}

class Bar(private val name: String) : TestCaseExtension {
   constructor() : this("bar")
   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      return TestResult.Success(0.seconds)
   }
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
private class MyAnnotatedSpec1 : FunSpec() {
   init {
      test("foo") { error("boom") }
   }
}

@ApplyExtension(Bar::class)
private class MyAnnotatedSpec2 : FunSpec() {
   init {
      test("foo") { error("boom") }
   }
}

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
