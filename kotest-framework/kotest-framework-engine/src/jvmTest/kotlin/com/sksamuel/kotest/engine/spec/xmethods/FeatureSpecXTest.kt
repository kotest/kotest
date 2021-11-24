package com.sksamuel.kotest.engine.spec.xmethods

import io.kotest.core.spec.style.FeatureSpec

class FeatureSpecXTest : FeatureSpec() {

   init {

      feature("a feature") {
         xfeature("nested xfeature should be ignored") {
            scenario("parent is ignored") {
               error("Boom")
            }
            error("Boom")
         }
         xscenario("nested xscenario should be ignored") {
            error("Boom")
         }
         xscenario("nested xscenario with config").config(invocations = 3) {
            error("Boom")
         }
      }

      xfeature("top level ignored feature") {
         scenario("parent is ignored") {
            error("Boom")
         }
         error("Boom")
      }
   }
}
