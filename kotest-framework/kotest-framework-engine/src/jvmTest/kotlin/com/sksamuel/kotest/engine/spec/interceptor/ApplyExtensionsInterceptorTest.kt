package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.DefaultExtensionRegistry
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.extensions.SpecWrapperExtension
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.ref.ApplyExtensionsInterceptor
import io.kotest.matchers.types.shouldBeInstanceOf

@EnabledIf(LinuxCondition::class)
class ApplyExtensionsInterceptorTest : FunSpec() {
   init {

      test("ApplyExtensionsInterceptor should apply extensions") {
         val registry = DefaultExtensionRegistry()
         ApplyExtensionsInterceptor(registry)
            .intercept(SpecRef.Reference(MyAnnotatedSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  val wrapper = registry.all().single() as SpecWrapperExtension
                  wrapper.delegate.shouldBeInstanceOf<Foo>()
                  return Result.success(emptyMap())
               }
            })
      }

      test("ApplyExtensionsInterceptor should apply extensions where the primary constructor is not no-args") {
         val registry = DefaultExtensionRegistry()
         ApplyExtensionsInterceptor(registry)
            .intercept(SpecRef.Reference(MyAnnotatedSpec2::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  val wrapper = registry.all().single() as SpecWrapperExtension
                  wrapper.delegate.shouldBeInstanceOf<Bar>()
                  return Result.success(emptyMap())
               }
            })
      }
   }
}

class Foo : Extension

class Bar(private val name: String) : Extension {
   constructor() : this("bar")
}

@ApplyExtension(Foo::class)
private class MyAnnotatedSpec : FunSpec()

@ApplyExtension(Bar::class)
private class MyAnnotatedSpec2 : FunSpec()
