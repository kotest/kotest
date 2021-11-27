package com.sksamuel.kotest.engine.spec.examples

import io.kotest.core.spec.style.FreeSpec

class FreeSpecExampleTest : FreeSpec() {
   init {
      "top level context" - {
         "a nested context" - {
            "a test" {
            }
         }
      }
      "a top level test" {

      }
   }
}
