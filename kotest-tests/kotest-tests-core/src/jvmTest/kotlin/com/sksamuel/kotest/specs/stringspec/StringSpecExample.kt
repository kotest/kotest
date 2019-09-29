package com.sksamuel.kotest.specs.stringspec

import io.kotest.specs.StringSpec

class StringSpecExample : StringSpec() {
  init {
    "this is a test" {
      // test here
    }
    "this test has config".config(enabled = false) {

    }
  }
}