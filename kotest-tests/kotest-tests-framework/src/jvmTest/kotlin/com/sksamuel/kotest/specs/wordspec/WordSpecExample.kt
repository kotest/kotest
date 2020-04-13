package com.sksamuel.kotest.specs.wordspec

import io.kotest.core.spec.style.WordSpec
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
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
            "support threads".config(invocations = 6, threads = 2) {

            }
         }
      }
   }
}
