package com.sksamuel.kotlintest.specs.feature

import io.kotlintest.matchers.integers.shouldBeLessThan
import io.kotlintest.specs.FeatureSpec

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