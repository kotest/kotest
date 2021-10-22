package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.engine.spec.interceptor.SpecEnterInterceptor
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class SpecEnterInterceptorTest : FunSpec() {
   init {
      test("SpecEnterInterceptor should call spec enter before invoking spec") {
         var result = ""
         val listener = object : AbstractTestEngineListener() {
            override suspend fun specEnter(kclass: KClass<*>) {
               result += "a"
            }
         }
         SpecEnterInterceptor(listener)
            .intercept {
               result += "b"
               emptyMap()
            }.invoke(ReflectiveSpecRef(FooSpec::class))
         result shouldBe "ab"
      }
   }
}

private class FooSpec : FunSpec()
