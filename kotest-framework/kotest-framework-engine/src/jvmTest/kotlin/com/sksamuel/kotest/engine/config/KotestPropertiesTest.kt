package com.sksamuel.kotest.engine.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.config.projectConfigResolver
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds

class KotestPropertiesTest : FunSpec() {
   init {
      test("properties file should be applied") {
         // override the kotest.properties filename so it's only applied to this test
         withSystemProperty(KotestEngineProperties.propertiesFilename, "/test.kotest.properties") {
            val listener = CollectingTestEngineListener()
            TestEngineLauncher(listener)
               .withClasses(C::class)
               .launch()
            listener.names shouldBe listOf("a")
            listener.result("a")!!.isSuccess.shouldBeTrue()
         }
      }
   }
}

private class C : FunSpec({
   test("a") {
      this.projectConfigResolver.projectTimeout() shouldBe 123.milliseconds
   }
})
