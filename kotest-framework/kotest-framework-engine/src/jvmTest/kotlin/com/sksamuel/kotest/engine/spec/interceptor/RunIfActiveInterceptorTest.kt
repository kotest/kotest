package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.config.Configuration
import io.kotest.core.listeners.InactiveSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.interceptor.RunIfActiveInterceptor
import io.kotest.matchers.booleans.shouldBeTrue

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

      test("RunIfActiveInterceptor should fire listeners on skip") {
         var fired = false
         val conf = Configuration()
         conf.register(object : InactiveSpecListener {
            override suspend fun inactive(spec: Spec, results: Map<TestCase, TestResult>) {
               fired = true
            }
         })
         RunIfActiveInterceptor(NoopTestEngineListener, conf)
            .intercept { error("boom") }
            .invoke(MyInactiveSpec())
         fired.shouldBeTrue()
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
