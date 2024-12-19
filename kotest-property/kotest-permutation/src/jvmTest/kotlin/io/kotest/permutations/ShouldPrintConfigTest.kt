package io.kotest.permutations

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.of

class ShouldPrintConfigTest : FunSpec() {
   init {
      test("should print config when enabled") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintConfig = true
               shouldPrintGeneratedValues = true
               maxFailures = 3
               failOnSeed = true
               val someprop by gen { Exhaustive.of(192, 568) }
               iterations = 2
               forEach {
                  someprop shouldNotBe null
               }
            }
         }
         stdout.shouldContain("Fail on seed: true")
         stdout.shouldContain("Print generated values: true")
         stdout.shouldContain("Max failures: 3")
      }

      test("should not print config when disabled") {
         val stdout = captureStandardOut {
            permutations {
               val someprop by gen { Exhaustive.of(192, 568) }
               iterations = 2
               forEach {
                  someprop shouldNotBe null
               }
            }
         }
         stdout.shouldNotContain("Permutation test config")
      }
   }
}
