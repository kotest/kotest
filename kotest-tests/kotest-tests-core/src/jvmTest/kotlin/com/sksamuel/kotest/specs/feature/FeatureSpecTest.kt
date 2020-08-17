package com.sksamuel.kotest.specs.feature

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.ints.shouldBeLessThan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FeatureSpecTest : FeatureSpec() {

   init {

      feature("a feature") {
         scenario("can execute a scenario") {
            1.shouldBeLessThan(4)
         }
         xfeature("xfeature should be ignored") {
            scenario("parent is ignored") {
               error("Boom")
            }
         }
         xscenario("xscenario should be ignored") {
            error("Boom")
         }
         xscenario("ignored and has config").config(invocations = 3) {
            error("Boom")
         }
      }

      xfeature("should be ignored") {
         error("Boom")
      }

      xfeature("should be ignored 2") {
         scenario("should be ignored") {
            error("Boom")
         }
      }

      feature("a feature with coroutine in feature scope") {
         launch { delay(1) }
         scenario("a dummy scenario") {

         }
      }
   }
}
