package io.kotest.samples.gradle

import io.kotest.specs.FreeSpec

class FreeSpecExampleTest : FreeSpec() {

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