package com.sksamuel.kotest.engine.datatest

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.datatest.forAll
import io.kotest.datatest.forNone
import io.kotest.matchers.shouldBe

@Ignored
internal class FeatureSpecDataTest : FeatureSpec() {
   init {
      data class PythagTriple(val a: Int, val b: Int, val c: Int)

      feature("datatest forAll") {
         forAll(
            PythagTriple(3, 4, 5),
            PythagTriple(6, 8, 10),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }
      }

      feature("datatest forAll failure") {
         forAll(
            PythagTriple(3, 2, 1),
            PythagTriple(4, 3, 2),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }
      }

      feature("datatest forNone") {
         forNone(
            PythagTriple(1, 2, 3),
            PythagTriple(2, 3, 4),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }
      }

      feature("datatest forNone failure") {
         forNone(
            PythagTriple(13, 84, 85),
            PythagTriple(16, 63, 65),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }
      }
   }
}
