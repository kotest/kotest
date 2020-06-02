package com.sksamuel.kotest.specs.feature

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.core.spec.style.FeatureSpec

class FeatureBangTest : FeatureSpec() {

   init {
      feature("!BangedFeature") {
         attemptToFail()
      }

      feature("NonBangedFeature") {
         feature("!BangedAnd") {
            attemptToFail()
         }

         feature("NonBangedAnd") {
            scenario("!BangedScenario") {
               attemptToFail()
            }
         }

         scenario("!BangedScenario") {
            attemptToFail()
         }
      }
   }
}
