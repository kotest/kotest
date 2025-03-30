package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.TestEngineInitializedInterceptor
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue

@EnabledIf(NotMacOnGithubCondition::class)
class TestEngineInitializeInterceptorTest : FunSpec({

   test("should invoke initialize") {
      var fired = false
      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineInitialized(context: EngineContext) {
            fired = true
         }
      }
      TestEngineInitializedInterceptor.intercept(
         EngineContext.empty.mergeListener(listener)
      ) { EngineResult.empty }
      fired.shouldBeTrue()
   }

})
