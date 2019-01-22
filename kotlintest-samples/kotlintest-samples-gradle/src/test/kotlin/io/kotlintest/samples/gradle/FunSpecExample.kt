package io.kotlintest.samples.gradle

import io.kotlintest.matchers.string.shouldBeLowerCase
import io.kotlintest.matchers.string.shouldNotBeBlank
import io.kotlintest.specs.FunSpec

class FunSpecExampleTest : FunSpec({

  test("a string cannot be blank") {
    "wibble".shouldNotBeBlank()
  }

  test("a string should be lower case") {
    "wibble".shouldBeLowerCase()
  }

})