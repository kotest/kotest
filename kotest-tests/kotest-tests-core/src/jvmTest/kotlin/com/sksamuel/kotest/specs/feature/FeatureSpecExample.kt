package com.sksamuel.kotest.specs.feature

import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.specs.FeatureSpec

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

    feature("this feature") {
      and("has nested feature contexts") {
        scenario("test without config") {
          1.shouldBeLessThan(4)
        }
        scenario("test with config").config(invocations = 4, threads = 2) {
          1.shouldBeLessThan(4)
        }
      }
    }
  }
}
