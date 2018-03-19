package io.kotlintest.core

import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec

class FeatureSpecLambdaTest : FeatureSpec({
  feature("string.length") {
    scenario("I call \"foobar.length\"") {
      "foobar".length shouldBe 6
    }
  }
})