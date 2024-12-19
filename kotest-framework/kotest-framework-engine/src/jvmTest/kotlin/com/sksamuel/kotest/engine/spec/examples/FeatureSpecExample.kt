package com.sksamuel.kotest.engine.spec.examples

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldHaveLength
import kotlin.time.Duration.Companion.seconds

class FeatureSpecExample : FeatureSpec() {
   init {

      feature("a top level feature") {
         scenario("some scenario") {
            1.shouldBeLessThan(4)
         }
         feature("a nested feature") {
            "a".shouldHaveLength(1)
            scenario("some nested scenario") {
               1.shouldBeLessThan(4)
            }
         }
      }

      feature("another feature") {
         scenario("test with config").config(enabled = true) {
            1.shouldBeLessThan(4)
         }
      }

      feature("this feature has config").config(timeout = 10.seconds) {
         scenario("nested scenario will inherit config") {
            1.shouldBeLessThan(4)
         }
         xfeature("disabled scenario") {
            error("Boom")
         }
      }

      xfeature("top level disabled feature") {
         error("Boom")
      }
   }
}
