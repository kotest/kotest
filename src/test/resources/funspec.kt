package io.kotest.samples.gradle

import io.kotlintest.matchers.string.shouldBeLowerCase
import io.kotlintest.matchers.string.shouldBeUpperCase
import io.kotlintest.matchers.string.shouldNotBeBlank
import io.kotlintest.specs.FunSpec

class FunSpecExampleTest : FunSpec({

  test("a string cannot be blank") {
    "wibble".shouldNotBeBlank()
  }

  test("a string should be lower case").config(enabled = true) {
    "wibble".shouldBeLowerCase()
  }

  context("some context") {

    test("a string cannot be blank") {
      "wibble".shouldNotBeBlank()
    }

    test("a string should be lower case").config(enabled = true) {
      "wibble".shouldBeLowerCase()
    }

    context("another context") {

      test("a string cannot be blank") {
        "wibble".shouldNotBeBlank()
      }

      test("a string should be lower case").config(enabled = true) {
        "wibble".shouldBeLowerCase()
        "WOBBLE".shouldBeUpperCase()
      }
    }
  }

})