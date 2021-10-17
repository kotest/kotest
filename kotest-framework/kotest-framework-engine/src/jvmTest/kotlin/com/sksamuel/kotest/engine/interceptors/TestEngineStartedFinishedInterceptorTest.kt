package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.TestEngineStartedFinishedInterceptor
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize

class TestEngineStartedFinishedInterceptorTest : FunSpec({

   test("should invoke engineStarted") {
      var fired = false
      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineStarted() {
            fired = true
         }
      }
      TestEngineStartedFinishedInterceptor.intercept(
         EngineContext.empty.mergeListener(listener)
      ) { EngineResult.empty }
      fired.shouldBeTrue()
   }

   test("should invoke engineFinished with errors") {
      var errors = emptyList<Throwable>()
      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineFinished(t: List<Throwable>) {
            errors = t
         }
      }
      TestEngineStartedFinishedInterceptor.intercept(
         EngineContext.empty.mergeListener(listener)
      ) { EngineResult(listOf(Exception("foo"))) }
      errors.shouldHaveSize(1)
   }
})
