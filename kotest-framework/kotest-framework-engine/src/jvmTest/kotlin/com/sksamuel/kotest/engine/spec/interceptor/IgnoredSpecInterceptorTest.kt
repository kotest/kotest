package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Ignored
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.IgnoredSpecListener
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.extensions.EmptyExtensionRegistry
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.ref.enabled.IgnoredSpecInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.reflect.KClass

@EnabledIf(LinuxOnlyGithubCondition::class)
class IgnoredSpecInterceptorTest : FunSpec({

   test("IgnoredSpecInterceptor should pass any class not annotated with @Ignored") {
      var fired = false
      IgnoredSpecInterceptor(NoopTestEngineListener, SpecExtensions())
         .intercept(SpecRef.Reference(NotIgnoredSpec::class), object : NextSpecRefInterceptor {
            override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
               fired = true
               return Result.success(emptyMap())
            }
         })
      fired.shouldBeTrue()
   }

   test("IgnoredSpecInterceptor should skip any spec annotated with @Ignored") {
      IgnoredSpecInterceptor(NoopTestEngineListener, SpecExtensions())
         .intercept(SpecRef.Reference(MyIgnoredSpec::class), object : NextSpecRefInterceptor {
            override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
               error("boom")
            }
         })
   }

   test("IgnoredSpecInterceptor should fire listeners on skip") {
      var fired = false
      val p = object : AbstractProjectConfig() {
         override val extensions = listOf(object : IgnoredSpecListener {
            override suspend fun ignoredSpec(kclass: KClass<*>, reason: String?) {
               fired = true
            }
         })
      }
      val c = SpecConfigResolver(p, EmptyExtensionRegistry)
      IgnoredSpecInterceptor(NoopTestEngineListener, SpecExtensions(c, ProjectConfigResolver(p)))
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
