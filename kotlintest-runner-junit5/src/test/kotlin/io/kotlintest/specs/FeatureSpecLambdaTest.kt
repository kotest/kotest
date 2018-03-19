package io.kotlintest.specs

import io.kotlintest.shouldBe

class FeatureSpecLambdaTest : FeatureSpec({
  feature("string.length") {
    scenario("I call \"foobar.length\"") {
      "foobar".length shouldBe 6
    }
  }
})