package com.sksamuel.kotest.specs.wordspec

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@UseExperimental(ExperimentalTime::class)
class WordSpecTest : WordSpec() {

   init {
      "a context" should {
         "have a test" {
            2.shouldBeGreaterThan(1)
         }
         "have another test" {
            2.shouldBeGreaterThan(1)
         }
         "have a test with config".config(enabled = false) {

         }
      }

      "another context" When {

         "using when" Should {
            "have a test" {
               2.shouldBeGreaterThan(1)
            }
            "have a test with config".config(timeout = 10000.milliseconds) {
               2.shouldBeGreaterThan(1)
            }
         }

      }
   }
}
