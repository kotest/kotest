package com.sksamuel.kotest.engine.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.config.projectConfigResolver
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe

class KotestPropertiesTest : FunSpec() {
   init {
      test("properties file should be applied") {
         // override the kotest.properties filename so it's only applied to this test
         withSystemProperty(KotestEngineProperties.PROPERTIES_FILENAME, "/test.kotest.properties") {
            TestEngineLauncher()
               .withListener(NoopTestEngineListener)
               .withClasses(C::class)
               .launch()
            value shouldBe 123
         }
      }
   }
}

private var value = 0L

private class C : FunSpec({
   test("a") {
      value = this.projectConfigResolver.projectTimeout()!!.inWholeMilliseconds
   }
})
