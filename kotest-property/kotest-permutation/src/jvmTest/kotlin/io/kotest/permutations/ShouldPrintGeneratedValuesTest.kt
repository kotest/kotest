package io.kotest.permutations

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.of

class ShouldPrintGeneratedValuesTest : FunSpec() {
   init {
      test("should print generated values when enabled") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintGeneratedValues = true
               failOnSeed = true
               val someprop by gen { Exhaustive.of(192, 568) }
               iterations = 2
               forEach {
                  someprop shouldNotBe null
               }
            }
         }
         stdout.shouldContain("someprop = 192")
         stdout.shouldContain("someprop = 568")
      }
   }
}
