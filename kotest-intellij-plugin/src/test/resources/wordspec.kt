package com.sksamuel.kotest.specs.wordspec

import io.kotest.core.spec.style.WordSpec
import io.kotest.datatest.withData

class WordSpecExample : WordSpec() {
  init {
    "some should context" should {
      "test something" {
        // test here
      }
      "test something with config".config(invocations = 2) {
      }
    }

    "with capital When" When {
      "and capital Should" Should {
        "test something"{}
        "test something with config".config(invocations = 2) {
        }
      }
       "!disabled should" should {

       }
    }

    "!disabled when" When {

    }

     withData(1, 2, 3, 4, 5) { value ->
        // test here
     }
  }
}
