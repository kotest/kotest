package com.sksamuel.kotest.specs.freespec

import io.kotest.core.spec.style.FreeSpec
import io.kotest.shouldThrow

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
