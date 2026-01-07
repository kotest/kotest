package com.sksamuel.kotest.specs.stringspec

import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData

class StringSpecExample : StringSpec() {
  init {
    "test" {
      // test here
    }
    "test with config".config(enabled = false) {

    }
     withData(1, 2, 3, 4, 5) { value ->
        // test here
     }
  }
}
