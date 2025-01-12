package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.IgnoredSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.ref.EnabledIfInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.reflect.KClass

@EnabledIf(LinuxCondition::class)
class EnabledIfSpecInterceptorTest : FunSpec({

   test("EnabledIfSpecInterceptor should proceed for any spec not annotated with @EnabledIf") {
      var fired = false
      EnabledIfInterceptor(NoopTestEngineListener, SpecExtensions())
         .intercept(SpecRef.Reference(MyUnannotatedSpec::class), object : NextSpecRefInterceptor {
            override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
               fired = true
               return Result.success(emptyMap())
            }
         })
      fired.shouldBeTrue()
   }

   test("EnabledIfSpecInterceptor should proceed any spec annotated with @EnabledIf that passes predicate") {
      var fired = false
      EnabledIfInterceptor(NoopTestEngineListener, SpecExtensions())
         .intercept(
            SpecRef.Reference(MyEnabledSpec::class),
            object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  fired = true
                  return Result.success(emptyMap())
               }
            })
      fired.shouldBeTrue()
   }

   test("EnabledIfSpecInterceptor should skip any spec annotated with @EnabledIf that fails predicate") {
      EnabledIfInterceptor(NoopTestEngineListener, SpecExtensions())
         .intercept(
            SpecRef.Reference(MyDisabledSpec::class),
            object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  error("boom")
               }
            })
   }

   test("EnabledIfSpecInterceptor should fire listeners on skip") {
      var fired = false
      val ext = object : IgnoredSpecListener {
         override suspend fun ignoredSpec(kclass: KClass<*>, reason: String?) {
            fired = true
         }
      }
      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(ext)
      }

      EnabledIfInterceptor(NoopTestEngineListener, SpecExtensions(SpecConfigResolver(c), ProjectConfigResolver(c)))
         .intercept(
            SpecRef.Reference(MyDisabledSpec::class),
            object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  error("boom")
               }
            })

      fired.shouldBeTrue()
   }
})

class MyEnabledCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = true
}

class MyDisabledCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = false
}


@EnabledIf(MyEnabledCondition::class)
private class MyEnabledSpec : FunSpec()

@EnabledIf(MyDisabledCondition::class)
private class MyDisabledSpec : FunSpec()

private class MyUnannotatedSpec : FunSpec()
