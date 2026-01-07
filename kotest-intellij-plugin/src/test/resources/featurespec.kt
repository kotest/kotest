package com.sksamuel.kotest.specs.feature

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.datatest.withData

class FeatureSpecExample : FeatureSpec() {
   init {

      feature("no scenario") {
         1.shouldBeLessThan(4)
      }

      feature("some feature") {
         scenario("some scenario") {
            1.shouldBeLessThan(4)
         }
      }

      feature("another feature") {
         scenario("test with config").config(invocations = 4, threads = 2) {
            1.shouldBeLessThan(4)
         }
      }

      feature("a feature with config").config(invocations = 4, threads = 2) {
         1.shouldBeLessThan(4)
      }

      feature("this feature") {
         feature("has nested feature contexts") {
            scenario("test without config") {
               1.shouldBeLessThan(4)
            }
            scenario("test with config").config(invocations = 4, threads = 2) {
               1.shouldBeLessThan(4)
            }
         }
      }
      withData(1, 2, 3, 4, 5) { value ->
         // test here
      }
   }
}
