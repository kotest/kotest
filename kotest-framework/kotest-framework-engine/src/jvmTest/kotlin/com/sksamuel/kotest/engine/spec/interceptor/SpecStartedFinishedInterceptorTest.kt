package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.engine.spec.interceptor.SpecStartedFinishedInterceptor
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class SpecStartedFinishedInterceptorTest : FunSpec() {
   init {

      test("SpecStartedFinishedInterceptor should call specStarted before invoking spec") {
         var result = ""
         val listener = object : AbstractTestEngineListener() {
            override suspend fun specStarted(kclass: KClass<*>) {
               result += "a"
            }
         }
         SpecStartedFinishedInterceptor(listener)
            .intercept {
               result += "b"
               emptyMap()
            }.invoke(ReflectiveSpecRef(FooSpec::class))
         result shouldBe "ab"
      }

      test("SpecExitInterceptor should call spec exit after invoking spec") {
         var result = ""
         val listener = object : AbstractTestEngineListener() {
            override suspend fun specFinished(kclass: KClass<*>, t: Throwable?) {
               result += "a"
            }
         }
         SpecStartedFinishedInterceptor(listener)
            .intercept {
               result += "b"
               emptyMap()
            }.invoke(ReflectiveSpecRef(FooSpec::class))
         result shouldBe "ba"
      }
   }
}

private class FooSpec : FunSpec()
