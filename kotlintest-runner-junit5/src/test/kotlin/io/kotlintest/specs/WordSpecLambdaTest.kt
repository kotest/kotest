package io.kotlintest.specs

import io.kotlintest.shouldBe

class WordSpecLambdaTest : WordSpec({

  var wibble = "sammy"

  "Testing Word Spec Lambas" should {
    wibble = "jammy"
    "a" {
      wibble shouldBe "jammy"
    }
  }
})