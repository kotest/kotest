package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.interceptors.TestEngineListenerInitializeFinalizeInterceptor
import io.kotest.engine.listener.TestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue

class TestEngineListenerInitializeFinalizeInterceptorTest : FunSpec({

   test("should invoke initialize") {
      var fired = false
      val listener = object : TestEngineListener {
         override suspend fun engineInitialize() {
            fired = true
         }
      }
      TestEngineListenerInitializeFinalizeInterceptor.intercept(
         TestSuite.empty,
         listener
      ) { _, _ -> EngineResult.empty }
      fired.shouldBeTrue()
   }

   test("should invoke finalize") {
      var fired = false
      val listener = object : TestEngineListener {
         override suspend fun engineFinalize() {
            fired = true
         }
      }
      TestEngineListenerInitializeFinalizeInterceptor.intercept(
         TestSuite.empty,
         listener
      ) { _, _ -> EngineResult.empty }
      fired.shouldBeTrue()
   }

})
