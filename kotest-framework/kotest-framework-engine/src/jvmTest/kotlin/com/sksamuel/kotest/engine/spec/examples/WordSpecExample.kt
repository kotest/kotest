package com.sksamuel.kotest.engine.spec.examples

import io.kotest.core.spec.style.WordSpec
import kotlin.time.Duration.Companion.milliseconds

class WordSpecExample : WordSpec() {
   init {

      "a stack" should {
         "return the last element when popped" {
            // test here
         }
         "push elements to the top".config(timeout = 1500.milliseconds) {
         }
      }

      "a queue" When {
         "iterated" Should {
            "return in insertion order"{
               // test here
            }
            "support removal".config(enabled = false) {
            }
         }
      }
   }
}
