package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Ignored
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.engine.config.EmptyExtensionRegistry
import io.kotest.engine.config.FixedExtensionRegistry
import io.kotest.core.listeners.IgnoredSpecListener
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.ref.IgnoredSpecInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.reflect.KClass

@EnabledIf(LinuxCondition::class)
class IgnoredSpecInterceptorTest : FunSpec({

   test("IgnoredSpecInterceptor should pass any class not annotated with @Ignored") {
      var fired = false
      IgnoredSpecInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
         .intercept(SpecRef.Reference(NotIgnoredSpec::class), object : NextSpecRefInterceptor {
            override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
               fired = true
               return Result.success(emptyMap())
            }
         })
      fired.shouldBeTrue()
   }

   test("IgnoredSpecInterceptor should skip any spec annotated with @Ignored") {
      IgnoredSpecInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
         .intercept(SpecRef.Reference(MyIgnoredSpec::class), object : NextSpecRefInterceptor {
            override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
               error("boom")
            }
         })
   }

   test("IgnoredSpecInterceptor should fire listeners on skip") {
      var fired = false
      val ext = object : IgnoredSpecListener {
         override suspend fun ignoredSpec(kclass: KClass<*>, reason: String?) {
            fired = true
         }
      }
      IgnoredSpecInterceptor(NoopTestEngineListener, FixedExtensionRegistry(ext))
         .intercept(SpecRef.Reference(MyIgnoredSpec::class), object : NextSpecRefInterceptor {
            override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
               error("boom")
            }
         })
      fired.shouldBeTrue()
   }
})

private class NotIgnoredSpec : FunSpec()

@Ignored
private class MyIgnoredSpec : FunSpec()
