package com.sksamuel.kotest.specs.wordspec

import io.kotlintest.specs.WordSpec

class WordSpecExample : WordSpec() {
  init {
    "some should context" should {
      "test something" {
        // test here
      }
      "allow config".config(invocations = 2) {
      }
    }

    "with capital When" When {
      "and capital Should" Should {
        "test something"{
          // test here
        }
        "allow config".config(invocations = 2) {
        }
      }
    }
  }
}