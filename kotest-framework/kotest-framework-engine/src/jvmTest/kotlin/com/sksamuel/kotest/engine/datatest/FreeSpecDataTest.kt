package com.sksamuel.kotest.engine.datatest

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.rollup
import io.kotest.matchers.shouldBe

class FreeSpecDataTest : FreeSpec() {
   init {
      data class PythagTriple(val a: Int, val b: Int, val c: Int)

      "nested data test happy path" - {
         rollup(
            PythagTriple(3, 4, 5),
            PythagTriple(6, 8, 10),
            PythagTriple(9, 12, 15),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }
      }

      "nested data test with failure" - {
         rollup(
            PythagTriple(9, 12, 12),
         ) { (a, b, c) ->
            shouldThrowAny {
               a * a + b * b shouldBe c * c
            }
         }
      }
   }
}
