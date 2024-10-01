package com.sksamuel.kotest.matchers.numerics

import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.ints.shouldNotBeBetween
import io.kotest.property.checkAll

class BetweenTest : ShouldSpec() {
   init {
      context("shouldBeBetween(Int, Int)") {
         should("pass only for ints in the range") {
            checkAll<Int, Int, Int> { a, b, c ->
               if (b in a..c) {
                  b.shouldBeBetween(a, c)
               } else {
                  b.shouldNotBeBetween(a, c)
               }
            }
         }
         should("use the correct error message") {
            shouldThrowMessage("1 should not be between (0, 2) inclusive") {
               1.shouldNotBeBetween(0, 2)
            }
         }
      }
   }
}
