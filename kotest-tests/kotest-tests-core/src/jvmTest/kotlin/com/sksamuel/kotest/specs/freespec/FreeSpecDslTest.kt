package com.sksamuel.kotest.specs.freespec

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec

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
