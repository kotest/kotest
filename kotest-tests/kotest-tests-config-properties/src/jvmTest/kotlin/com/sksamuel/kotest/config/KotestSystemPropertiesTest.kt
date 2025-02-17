package com.sksamuel.kotest.config

import io.kotest.core.annotation.Description
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@Description("Tests that the kotest.properties file is picked up")
class KotestSystemPropertiesTest : FunSpec() {
   init {
      test("FQN from kotest.properties should be used when defined") {
         MyProjectConfig.initialized shouldBe "yes, and before project"
      }
   }
}
