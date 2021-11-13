package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.common.ExperimentalKotest
import io.kotest.core.annotation.Ignored
import io.kotest.core.config.EmptyExtensionRegistry
import io.kotest.core.config.FixedExtensionRegistry
import io.kotest.core.listeners.IgnoredSpecListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.engine.spec.interceptor.IgnoredSpecInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.reflect.KClass

@ExperimentalKotest
class IgnoredSpecInterceptorTest : FunSpec({

   test("IgnoredSpecInterceptor should pass any class not annotated with @Ignored") {
      var fired = false
      IgnoredSpecInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
         .intercept(ReflectiveSpecRef(NotIgnoredSpec::class)) {
            fired = true
            Result.success(emptyMap())
         }
      fired.shouldBeTrue()
   }

   test("IgnoredSpecInterceptor should skip any spec annotated with @Ignored") {
      IgnoredSpecInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
         .intercept(ReflectiveSpecRef(MyIgnoredSpec::class)) { error("boom") }
   }

   test("IgnoredSpecInterceptor should fire listeners on skip") {
      var fired = false
      val ext = object : IgnoredSpecListener {
         override suspend fun ignoredSpec(kclass: KClass<*>, reason: String?) {
            fired = true
         }
      }
      IgnoredSpecInterceptor(NoopTestEngineListener, FixedExtensionRegistry(ext))
         .intercept(ReflectiveSpecRef(MyIgnoredSpec::class)) { error("boom") }
      fired.shouldBeTrue()
   }
})

private class NotIgnoredSpec : FunSpec()

@Ignored
private class MyIgnoredSpec : FunSpec()
