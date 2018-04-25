package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.matchers.numerics.shouldBeLessThan
import io.kotlintest.specs.FeatureSpec


class FeatureSpecExample : FeatureSpec() {
  init {
    feature("some feature") {
      scenario("some scenario") {
      }
    }
    feature("another feature") {
      scenario("test with config").config(invocations = 4, threads = 2) {
        1.shouldBeLessThan(4)
      }
    }
  }
}