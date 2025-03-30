package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.AlwaysFalseCondition
import io.kotest.core.annotation.AlwaysTrueCondition
import io.kotest.core.annotation.DisabledIf
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.IgnoredSpecListener
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.ref.enabled.DisabledIfInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.reflect.KClass

class DisabledIfInterceptorTest : FunSpec({

   test("DisabledIfInterceptor should proceed for any spec not annotated with @DisabledIf") {
      var fired = false
      DisabledIfInterceptor(NoopTestEngineListener, SpecExtensions())
         .intercept(SpecRef.Reference(UnannotatedSpec::class), object : NextSpecRefInterceptor {
            override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
               fired = true
               return Result.success(emptyMap())
            }
         })
      fired.shouldBeTrue()
   }

   test("DisabledIfInterceptor should proceed any spec annotated with @DisabledIf that fails predicate") {
      var fired = false
      DisabledIfInterceptor(NoopTestEngineListener, SpecExtensions())
         .intercept(
            SpecRef.Reference(FalseSpec::class),
            object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  fired = true
                  return Result.success(emptyMap())
               }
            })
      fired.shouldBeTrue()
   }

   test("DisabledIfInterceptor should skip any spec annotated with @DisabledIf that fails predicate") {
      DisabledIfInterceptor(NoopTestEngineListener, SpecExtensions())
         .intercept(
            SpecRef.Reference(TrueSpec::class),
            object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  error("boom")
               }
            })
   }

   test("DisabledIfInterceptor should fire listeners on skip") {
      var fired = false
      val ext = object : IgnoredSpecListener {
         override suspend fun ignoredSpec(kclass: KClass<*>, reason: String?) {
            fired = true
         }
      }
      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(ext)
      }

      DisabledIfInterceptor(NoopTestEngineListener, SpecExtensions(SpecConfigResolver(c), ProjectConfigResolver(c)))
         .intercept(
            SpecRef.Reference(TrueSpec::class),
            object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  error("boom")
               }
            })

      fired.shouldBeTrue()
   }
})

@DisabledIf(AlwaysTrueCondition::class)
private class TrueSpec : FunSpec()

@DisabledIf(AlwaysFalseCondition::class)
private class FalseSpec : FunSpec()

private class UnannotatedSpec : FunSpec()
