package com.sksamuel.kotest.engine.spec.bangs

import io.kotest.core.spec.style.FeatureSpec

class FeatureBangTest : FeatureSpec() {

   init {
      feature("!BangedFeature") {
         error("RIP!")
      }

      feature("NonBangedFeature") {
         feature("!BangedAnd") {
            error("UGGH!")
         }

         feature("NonBangedAnd") {
            scenario("!BangedScenario") {
               error("VRONK!")
            }
         }

         scenario("!BangedScenario") {
            error("WHACK!")
         }
      }
   }
}
