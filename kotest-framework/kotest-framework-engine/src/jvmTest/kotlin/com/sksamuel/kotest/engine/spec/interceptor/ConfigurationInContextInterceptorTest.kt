package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.interceptor.ConfigurationInContextInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class ConfigurationInContextInterceptorTest : FunSpec() {
   init {
      test("config should be injected into the test context") {
         var fired = false
         val c = Configuration()
         ConfigurationInContextInterceptor(c).intercept {
            fired = true
            this.configuration shouldBe c
            emptyMap()
         }.invoke(DummySpec())
         fired.shouldBeTrue()
      }
   }
}

private class DummySpec : FunSpec()
