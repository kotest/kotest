package io.kotest.samples.gradle

import io.kotest.matchers.string.shouldBeLowerCase
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.specs.FunSpec

class FunSpecExampleTest : FunSpec({

  test("a string cannot be blank") {
    "wibble".shouldNotBeBlank()
  }

  test("a string should be lower case") {
    "wibble".shouldBeLowerCase()
  }

})