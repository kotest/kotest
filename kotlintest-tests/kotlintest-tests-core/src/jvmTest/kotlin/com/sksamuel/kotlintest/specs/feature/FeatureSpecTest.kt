package com.sksamuel.kotlintest.specs.feature

import io.kotlintest.matchers.integers.shouldBeLessThan
import io.kotlintest.specs.FeatureSpec

class FeatureSpecTest : FeatureSpec() {

  init {

    feature("a feature") {
      scenario("can execute a scenario") {
        1.shouldBeLessThan(4)
      }
    }
  }
}