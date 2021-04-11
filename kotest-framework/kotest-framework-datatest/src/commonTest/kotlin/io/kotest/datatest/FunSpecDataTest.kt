package io.kotest.datatest

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

internal class FunSpecDataTest : FunSpec() {
   init {
      data class PythagTriple(val a: Int, val b: Int, val c: Int)

      forAll(2, 4, 6) {
         withClue("$it is even check") {
            it % 2 shouldBe 0
         }
      }

      forAll("a", "b") { a ->
         forAll("x", "y") { b ->
            a + b shouldHaveLength 2
         }
      }

      forAll("r", "s") { a ->
         forAll("x", "y") { b ->
            forAll("p", "q") { c ->
               a + b + c shouldHaveLength 3
            }
         }
      }

      context("scoped forAll") {
         forAll(2, 4, 6) {
            withClue("$it is even check") {
               it % 2 shouldBe 0
            }
         }
      }

//      forNone(1, 3, 5) {
//         withClue("$it is even check") {
//            it % 2 shouldBe 0
//         }
//      }
//
//      forNone("x", "y") { a ->
//         forNone("a", "b") { b ->
//            a + b shouldHaveLength 3
//         }
//      }

//      context("scoped forAll") {
//         forAll(
//            PythagTriple(3, 4, 5),
//            PythagTriple(6, 8, 10),
//         ) { (a, b, c) ->
//            a * a + b * b shouldBe c * c
//         }
//      }
//
//      context("scoped forAll failure") {
//         forAll(
//            PythagTriple(3, 2, 1),
//            PythagTriple(4, 3, 2),
//         ) { (a, b, c) ->
//            a * a + b * b shouldBe c * c
//         }
//      }
//
//      context("scoped forNone") {
//         forNone(
//            PythagTriple(1, 2, 3),
//            PythagTriple(2, 3, 4),
//         ) { (a, b, c) ->
//            a * a + b * b shouldBe c * c
//         }
//      }
//
//      context("scoped forNone failure") {
//         forNone(
//            PythagTriple(13, 84, 85),
//            PythagTriple(16, 63, 65),
//         ) { (a, b, c) ->
//            a * a + b * b shouldBe c * c
//         }
//      }
   }
}
