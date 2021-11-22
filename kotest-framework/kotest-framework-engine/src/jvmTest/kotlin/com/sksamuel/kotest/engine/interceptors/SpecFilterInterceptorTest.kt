package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.config.Configuration
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.SpecFilterResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.engine.spec.interceptor.SpecFilterInterceptor
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.reflect.KClass

class SpecFilterInterceptorTest : FunSpec() {
   init {
      test("spec filter's should filter tests") {
         val c = Configuration()
         c.registry.add(object : SpecFilter {
            override fun filter(kclass: KClass<*>): SpecFilterResult {
               return if (kclass.simpleName == "FooSpec") SpecFilterResult.Exclude("foo") else SpecFilterResult.Include
            }
         })
         var fired = false
         SpecFilterInterceptor(NoopTestEngineListener, c.registry).intercept(ReflectiveSpecRef(FooSpec::class)) {
            fired = true
            Result.success(emptyMap())
         }
         fired.shouldBeFalse()

         SpecFilterInterceptor(NoopTestEngineListener, c.registry).intercept(ReflectiveSpecRef(BarSpec::class)) {
            fired = true
            Result.success(emptyMap())
         }
         fired.shouldBeTrue()
      }
   }
}

private class FooSpec : FunSpec()
private class BarSpec : FunSpec()
