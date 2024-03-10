package com.sksamuel.kotest.engine.config

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.config.configuration
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class KotestPropertiesTest : FunSpec() {
   init {
      test("properties file should be applied") {
         // override the kotest.properties filename so it's only applied to this test
         withSystemProperty(KotestEngineProperties.propertiesFilename, "/test.kotest.properties") {
            val c = ProjectConfiguration()
            c.includePrivateClasses = true
            val listener = CollectingTestEngineListener()
            TestEngineLauncher(listener)
               .withConfiguration(c)
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
      this.configuration.parallelism shouldBe 15
   }
})
