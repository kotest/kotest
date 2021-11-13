package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.interceptor.ConfigurationInContextInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.coroutines.coroutineContext

class ConfigurationInContextInterceptorTest : FunSpec() {
   init {

      val c = Configuration()

      suspend fun testConfig() {
         coroutineContext.configuration shouldBe c
      }

      test("config should be injected into the test context") {
         var fired = false
         ConfigurationInContextInterceptor(c).intercept(DummySpec()) {
            testConfig()
            fired = true
            Result.success(emptyMap())
         }
         fired.shouldBeTrue()
      }
   }


}

private class DummySpec : FunSpec()
