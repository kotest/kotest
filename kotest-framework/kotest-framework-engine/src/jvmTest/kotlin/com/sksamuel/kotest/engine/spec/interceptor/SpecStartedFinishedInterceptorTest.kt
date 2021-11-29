package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.listener.Node
import io.kotest.engine.spec.interceptor.SpecFinishedInterceptor
import io.kotest.engine.spec.interceptor.SpecStartedInterceptor
import io.kotest.matchers.shouldBe

class SpecStartedFinishedInterceptorTest : FunSpec() {
   init {

      test("SpecStartedInterceptor should call specStarted before invoking spec") {
         var result = ""
         val listener = object : AbstractTestEngineListener() {
            override suspend fun executionStarted(node: Node) {
               if (node is Node.Spec)  result += "a"
            }
         }
         SpecStartedInterceptor(listener)
            .intercept(SpecRef.Reference(FooSpec::class)) {
               result += "b"
               Result.success(emptyMap())
            }
         result shouldBe "ab"
      }

      test("SpecFinishedInterceptor should call specFinished after invoking spec") {
         var r = ""
         val listener = object : AbstractTestEngineListener() {
            override suspend fun executionFinished(node: Node, result: TestResult) {
               if (node is Node.Spec) r += "a"
            }
         }
         SpecFinishedInterceptor(listener)
            .intercept(SpecRef.Reference(FooSpec::class)) {
               r += "b"
               Result.success(emptyMap())
            }
         r shouldBe "ba"
      }
   }
}

private class FooSpec : FunSpec()
