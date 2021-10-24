package com.sksamuel.kotest.engine.spec.style

import io.kotest.core.spec.style.FreeSpec

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
    "a test without a context block" {
    }
  }
}
