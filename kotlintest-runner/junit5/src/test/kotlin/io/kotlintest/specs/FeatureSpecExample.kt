package io.kotlintest.specs

import io.kotlintest.runner.junit5.specs.FeatureSpec


class FeatureSpecExample : FeatureSpec() {
  init {
    feature("some feature") {
      scenario("some scenario") {
      }
    }
  }
}