package com.sksamuel.kotest.specs.feature

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldHaveLength

class FeatureSpecExample : FeatureSpec() {
   init {

      feature("no scenario") {
         1.shouldBeLessThan(4)
      }

      feature("some feature") {
         scenario("some scenario") {
            1.shouldBeLessThan(4)
         }
         feature("a nested feature") {
            "a".shouldHaveLength(1)
         }
      }

      feature("another feature") {
         scenario("test with config").config(enabled = true) {
            1.shouldBeLessThan(4)
         }
      }

      feature("this feature") {
         feature("has nested feature contexts") {
            scenario("test without config") {
               1.shouldBeLessThan(4)
            }
            scenario("test with config").config(enabled = false) {
               fail("never executed")
            }
         }
      }
   }
}
