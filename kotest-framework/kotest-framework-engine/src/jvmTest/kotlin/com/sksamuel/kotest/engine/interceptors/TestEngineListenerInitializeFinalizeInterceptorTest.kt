package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.TestEngineInitializedInterceptor
import io.kotest.engine.listener.TestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue

class TestEngineListenerInitializeFinalizeInterceptorTest : FunSpec({

   test("should invoke initialize") {
      var fired = false
      val listener = object : TestEngineListener {
         override suspend fun engineStarted() {
            fired = true
         }
      }
      TestEngineInitializedInterceptor.intercept(
         EngineContext.empty.mergeListener(listener)
      ) { EngineResult.empty }
      fired.shouldBeTrue()
   }

})
