package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.config.configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.interceptor.instance.ConfigurationInContextSpecInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.coroutines.coroutineContext

@EnabledIf(LinuxCondition::class)
class ConfigurationInContextInterceptorTest : FunSpec() {
   init {

      val c = ProjectConfiguration()

      suspend fun testConfig() {
         coroutineContext.configuration shouldBe c
      }

      test("config should be injected into the test context") {
         var fired = false
         ConfigurationInContextSpecInterceptor(c).intercept(DummySpec()) {
            testConfig()
            fired = true
            Result.success(emptyMap())
         }
         fired.shouldBeTrue()
      }
   }


}

private class DummySpec : FunSpec()
