package com.sksamuel.kotest.specs.stringspec

import io.kotest.core.spec.style.StringSpec

class StringSpecExample : StringSpec() {
  init {
    "this is a test" {
      // test here
    }
    "this test has config".config(enabled = false) {

    }
  }
}
