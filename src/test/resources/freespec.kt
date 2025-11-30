package com.sksamuel.kotest.specs.freespec

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData

class FreeSpecExample : FreeSpec() {
  init {
    "some context" - {
      "more context" - {
        "as many as you want" - {
          "then a test" {
            // test case
          }
        }
      }
    }
    "another context" - {
      "a test with config".config(enabled = true) {

      }
    }
    "a test without a context block" {
    }

     withData(1, 2, 3, 4, 5) { value ->
        // test here
     }
  }
}
