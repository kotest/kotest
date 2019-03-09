package com.sksamuel.kotlintest.specs.stringspec

import io.kotlintest.specs.StringSpec

class StringSpecExample : StringSpec() {
  init {
    "this is a test" {
      // test here
    }
    "this test has config".config(enabled = false) {

    }
  }
}