package com.sksamuel.kotlintest.specs.stringspec

import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class StringSpecDslTest : StringSpec() {
  init {
    "allowed" {
      shouldThrow<Exception> {
        "this should throw at runtime" {

        }
      }
    }
  }
}