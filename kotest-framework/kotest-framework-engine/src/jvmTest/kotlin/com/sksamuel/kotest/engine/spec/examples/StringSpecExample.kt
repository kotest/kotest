package com.sksamuel.kotest.engine.spec.examples

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
