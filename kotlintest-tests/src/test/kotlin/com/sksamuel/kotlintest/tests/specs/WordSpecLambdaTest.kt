package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
class WordSpecLambdaTest : WordSpec({

  var wibble = "sammy"

  "Testing Word Spec Lambas" should {
    wibble = "jammy"
    "a" {
      wibble shouldBe "jammy"
    }
  }
})