package io.kotest.permutations

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.constant

class ShouldPrintGeneratedValuesTest : FunSpec() {
   init {
      test("should print generated values when enabled") {
         captureStandardOut {
            permutations {
               val a by gen { Exhaustive.constant(324234324) }
               forEach {
               }
            }
         }.shouldContain("324234324")
      }
   }
}
