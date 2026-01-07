package io.kotest.samples.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldBeLowerCase
import io.kotest.matchers.string.shouldBeUpperCase
import io.kotest.matchers.string.shouldNotBeBlank

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
