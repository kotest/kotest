package com.sksamuel.kotest.specs.freespec

import io.kotest.shouldThrow
import io.kotest.specs.FreeSpec

class FreeSpecDslTest : FreeSpec() {

  init {

    "context" - {
      "another context" - {
        "the test" {
          shouldThrow<Exception> {
            "no more test scopes allowed" {

            }
          }
        }
      }
    }
  }
}