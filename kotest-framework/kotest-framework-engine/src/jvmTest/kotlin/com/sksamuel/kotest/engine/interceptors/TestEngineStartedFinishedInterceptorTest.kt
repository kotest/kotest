package com.sksamuel.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.engine.EngineResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.TestEngineStartedFinishedInterceptor
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.listener.Node
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

@KotestInternal
class TestEngineStartedFinishedInterceptorTest : FunSpec({

   test("should invoke engineStarted before downstream") {
      var fired = ""
      val listener = object : AbstractTestEngineListener() {
         override suspend fun executionStarted(node: Node) {
            if (node is Node.Engine) fired += "a"
         }
      }
      TestEngineStartedFinishedInterceptor.intercept(
         EngineContext.empty.mergeListener(listener)
      ) {
         fired += "b"
         EngineResult.empty
      }
      fired.shouldBe("ab")
   }

   test("should invoke engineFinished after downstream") {
      var fired = ""
      val listener = object : AbstractTestEngineListener() {
         override suspend fun executionFinished(node: Node, result: TestResult) {
            if (node is Node.Engine) fired += "a"
         }
      }
      TestEngineStartedFinishedInterceptor.intercept(
         EngineContext.empty.mergeListener(listener)
      ) {
         fired += "b"
         EngineResult(listOf(Exception("foo")))
      }
      fired.shouldBe("ba")
   }

   test("should invoke engineFinished with errors") {
      var errors = emptyList<Throwable>()
      val listener = object : AbstractTestEngineListener() {
         override suspend fun executionFinished(node: Node, result: TestResult) {
            if (node is Node.Engine) {
               errors = listOfNotNull(result.errorOrNull)
            }
         }
      }
      TestEngineStartedFinishedInterceptor.intercept(
         EngineContext.empty.mergeListener(listener)
      ) { EngineResult(listOf(Exception("foo"))) }
      errors.shouldHaveSize(1)
   }
})
