package com.sksamuel.kotest.specs.feature

import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.specs.FeatureSpec

class FeatureSpecTest : FeatureSpec() {

  init {

    feature("a feature") {
      scenario("can execute a scenario") {
        1.shouldBeLessThan(4)
      }
    }
  }
}
