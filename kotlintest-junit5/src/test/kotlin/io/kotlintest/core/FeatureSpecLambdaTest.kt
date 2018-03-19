package io.kotlintest.core

import io.kotlintest.core.specs.FeatureSpec
import io.kotlintest.matchers.shouldBe

class FeatureSpecLambdaTest : FeatureSpec({
  feature("string.length") {
    scenario("I call \"foobar.length\"") {
      "foobar".length shouldBe 6
    }
  }
})