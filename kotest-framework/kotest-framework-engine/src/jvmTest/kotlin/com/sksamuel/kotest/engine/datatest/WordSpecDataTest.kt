package com.sksamuel.kotest.engine.datatest

import io.kotest.assertions.withClue
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.datatest.forAll
import io.kotest.core.datatest.forNone
import io.kotest.matchers.shouldBe

@Ignored// this is used by the DataTest itself, rather than being a stand alone test
internal class WordSpecDataTest : WordSpec() {
   init {
      data class PythagTriple(val a: Int, val b: Int, val c: Int)

      forAll(2, 4, 6) {
         withClue("$it is even check") {
            it % 2 shouldBe 0
         }
      }

      forNone(1, 3, 5) {
         withClue("$it is even check") {
            it % 2 shouldBe 0
         }
      }

      "datatest forAll" should {
         forAll(
            PythagTriple(3, 4, 5),
            PythagTriple(6, 8, 10),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }
      }

      "datatest forAll failure" should {
         forAll(
            PythagTriple(3, 2, 1),
            PythagTriple(4, 3, 2),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }
      }

      "datatest forNone" should {
         forNone(
            PythagTriple(1, 2, 3),
            PythagTriple(2, 3, 4),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }
      }

      "datatest forNone failure" should {
         forNone(
            PythagTriple(13, 84, 85),
            PythagTriple(16, 63, 65),
         ) { (a, b, c) ->
            a * a + b * b shouldBe c * c
         }
      }
   }
}
