package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.engine.spec.interceptor.SpecFinishedInterceptor
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class SpecExitInterceptorTest : FunSpec() {
   init {
      test("SpecExitInterceptor should call spec exit after invoking spec") {
         var result = ""
         val listener = object : AbstractTestEngineListener() {
            override suspend fun specFinished(kclass: KClass<*>, t: Throwable?) {
               result += "a"
            }
         }
         SpecFinishedInterceptor(listener)
            .intercept {
               result += "b"
               emptyMap()
            }.invoke(ReflectiveSpecRef(BarSpec::class))
         result shouldBe "ba"
      }
   }
}

private class BarSpec : FunSpec()
