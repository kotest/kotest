package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll

class SetsTest: StringSpec() {
   init {
      "generate sets of required length" {
         checkAll(1_000, Arb.set(
            Arb.double(),
            range = 3..4,
            slippage = 10,
            )) {
            it.size.shouldBeIn(3, 4)
         }
      }
      "handle case where not enough distinct elements" {
         val thrown = shouldThrow<IllegalStateException> {
            checkAll(
               1_000, Arb.set(
                  Arb.Companion.int(range = 1..2),
                  range = 3..4,
                  slippage = 10,
               )
            ) {
               it.size.shouldBeIn(3, 4)
            }
         }
         thrown.message.shouldContain("the target size requirement of")
         thrown.message.shouldContain("could not be satisfied after 0 consecutive samples")
      }
   }
}
