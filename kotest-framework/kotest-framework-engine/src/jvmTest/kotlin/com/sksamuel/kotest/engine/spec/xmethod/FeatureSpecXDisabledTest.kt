package com.sksamuel.kotest.engine.spec.xmethod

import io.kotest.core.spec.style.FeatureSpec

class FeatureSpecXDisabledTest : FeatureSpec() {

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
