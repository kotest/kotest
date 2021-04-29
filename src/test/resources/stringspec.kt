package com.sksamuel.kotest.specs.stringspec

import io.kotest.core.spec.style.StringSpec

class StringSpecExample : StringSpec() {
  init {
    "test" {
      // test here
    }
    "test with config".config(enabled = false) {

    }
  }
}
