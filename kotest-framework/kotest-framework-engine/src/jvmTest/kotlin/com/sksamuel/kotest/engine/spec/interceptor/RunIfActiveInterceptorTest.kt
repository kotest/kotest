package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.config.Configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.interceptor.RunIfActiveInterceptor

class RunIfActiveInterceptorTest : FunSpec() {
   init {

      test("RunIfActiveInterceptor should skip spec if not active") {
         RunIfActiveInterceptor(NoopTestEngineListener, Configuration())
            .intercept { error("foo") }
            .invoke(MyInactiveSpec())
      }

      test("RunIfActiveInterceptor should execute spec if active") {
         shouldThrowAny {
            RunIfActiveInterceptor(NoopTestEngineListener, Configuration())
               .intercept { error("foo") }
               .invoke(MyActiveSpec())
         }
      }
   }
}

private class MyInactiveSpec : FunSpec() {
   init {
      test("!disabled") {}
   }
}

private class MyActiveSpec() : FunSpec() {
   init {
      test("enabled") {}
   }
}
