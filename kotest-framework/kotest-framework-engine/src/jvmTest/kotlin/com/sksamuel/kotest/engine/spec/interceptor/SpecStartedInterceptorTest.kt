package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.engine.spec.interceptor.SpecStartedInterceptor
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class SpecStartedInterceptorTest : FunSpec() {
   init {
      test("SpecStartedInterceptor should call specStarted before invoking spec") {
         var result = ""
         val listener = object : AbstractTestEngineListener() {
            override suspend fun specStarted(kclass: KClass<*>) {
               result += "a"
            }
         }
         SpecStartedInterceptor(listener)
            .intercept {
               result += "b"
               emptyMap()
            }.invoke(ReflectiveSpecRef(FooSpec::class))
         result shouldBe "ab"
      }
   }
}

private class FooSpec : FunSpec()
