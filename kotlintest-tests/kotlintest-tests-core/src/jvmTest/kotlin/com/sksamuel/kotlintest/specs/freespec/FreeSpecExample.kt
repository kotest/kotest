package com.sksamuel.kotlintest.specs.freespec

import io.kotlintest.specs.FreeSpec

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