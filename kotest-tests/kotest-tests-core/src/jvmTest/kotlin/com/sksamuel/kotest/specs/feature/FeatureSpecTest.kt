package com.sksamuel.kotest.specs.feature

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.ints.shouldBeLessThan

class FeatureSpecTest : FeatureSpec() {

  init {

    feature("a feature") {
      scenario("can execute a scenario") {
        1.shouldBeLessThan(4)
      }
    }
  }
}
