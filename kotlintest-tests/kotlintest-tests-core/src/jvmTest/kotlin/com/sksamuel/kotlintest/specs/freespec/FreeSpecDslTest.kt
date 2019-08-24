package com.sksamuel.kotlintest.specs.freespec

import io.kotlintest.shouldThrow
import io.kotlintest.specs.FreeSpec

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