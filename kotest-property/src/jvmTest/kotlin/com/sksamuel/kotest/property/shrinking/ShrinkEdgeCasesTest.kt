package com.sksamuel.kotest.property.shrinking

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.IntShrinker
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.checkAll

class ShrinkEdgeCasesTest : FunSpec() {
   init {
      test("should shrink edge cases") {
         val arb: Arb<Int> = arbitrary(listOf(1, 2, 3, 4, 5), IntShrinker(1..5)) { 0 }
         try {
            checkAll(100, arb) {
               it shouldBe 0
            }
         } catch (e: Throwable) {
            e.message.shouldContain("shrunk from")
         }
      }
   }
}
