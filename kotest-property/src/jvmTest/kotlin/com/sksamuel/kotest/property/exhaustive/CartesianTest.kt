package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.Tuple4
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.cartesian
import io.kotest.property.exhaustive.cartesianPairs
import io.kotest.property.exhaustive.cartesianTriples
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.of

class CartesianTest : FunSpec() {
   init {

      test("Exhaustive.cartesianPairs") {
         listOf(1, 2, 3).exhaustive().cartesianPairs().values shouldBe listOf(
            Pair(1, 1),
            Pair(1, 2),
            Pair(1, 3),
            Pair(2, 1),
            Pair(2, 2),
            Pair(2, 3),
            Pair(3, 1),
            Pair(3, 2),
            Pair(3, 3),
         )
      }

      test("Exhaustive.cartesianTriples") {
         listOf(1, 2, 3).exhaustive().cartesianTriples().values shouldBe listOf(
            Triple(1, 1, 1),
            Triple(1, 1, 2),
            Triple(1, 1, 3),
            Triple(1, 2, 1),
            Triple(1, 2, 2),
            Triple(1, 2, 3),
            Triple(1, 3, 1),
            Triple(1, 3, 2),
            Triple(1, 3, 3),
            Triple(2, 1, 1),
            Triple(2, 1, 2),
            Triple(2, 1, 3),
            Triple(2, 2, 1),
            Triple(2, 2, 2),
            Triple(2, 2, 3),
            Triple(2, 3, 1),
            Triple(2, 3, 2),
            Triple(2, 3, 3),
            Triple(3, 1, 1),
            Triple(3, 1, 2),
            Triple(3, 1, 3),
            Triple(3, 2, 1),
            Triple(3, 2, 2),
            Triple(3, 2, 3),
            Triple(3, 3, 1),
            Triple(3, 3, 2),
            Triple(3, 3, 3),
         )
      }

      test("Exhaustive.cartesian(a,b) arity 2") {
         val e = Exhaustive.cartesian(
            Exhaustive.of(1, 2, 3),
            Exhaustive.of(true, false)
         ) { a, b -> Pair(a, b) }
         e.values.shouldHaveSize(6)
         e.values shouldBe listOf(
            Pair(1, true),
            Pair(1, false),
            Pair(2, true),
            Pair(2, false),
            Pair(3, true),
            Pair(3, false),
         )
      }

      test("a.cartesian(b) arity 2") {
         val e = Exhaustive.of(1, 2, 3).cartesian(Exhaustive.of(true, false)) { a, b -> Pair(a, b) }
         e.values.shouldHaveSize(6)
         e.values shouldBe listOf(
            Pair(1, true),
            Pair(1, false),
            Pair(2, true),
            Pair(2, false),
            Pair(3, true),
            Pair(3, false),
         )
      }


      test("a.cartesianPairs(b) arity 2") {
         val e = Exhaustive.of(1, 2, 3).cartesianPairs(Exhaustive.of(true, false))
         e.values.shouldHaveSize(6)
         e.values shouldBe listOf(
            Pair(1, true),
            Pair(1, false),
            Pair(2, true),
            Pair(2, false),
            Pair(3, true),
            Pair(3, false),
         )
      }

      test("Exhaustive.cartesianPairs(a,b) arity 2") {
         val e = Exhaustive.cartesianPairs(
            Exhaustive.of(1, 2, 3),
            Exhaustive.of(true, false)
         )
         e.values.shouldHaveSize(6)
         e.values shouldBe listOf(
            Pair(1, true),
            Pair(1, false),
            Pair(2, true),
            Pair(2, false),
            Pair(3, true),
            Pair(3, false),
         )
      }

      test("Exhaustive.cartesianTriples(a,b,c) arity 3") {
         val e = Exhaustive.cartesianTriples(
            Exhaustive.of(1, 2, 3),
            Exhaustive.of("a", "b", "c"),
            Exhaustive.of(true, false)
         )
         e.values.shouldHaveSize(18)
         e.values shouldBe listOf(
            Triple(1, "a", true),
            Triple(1, "a", false),
            Triple(1, "b", true),
            Triple(1, "b", false),
            Triple(1, "c", true),
            Triple(1, "c", false),
            Triple(2, "a", true),
            Triple(2, "a", false),
            Triple(2, "b", true),
            Triple(2, "b", false),
            Triple(2, "c", true),
            Triple(2, "c", false),
            Triple(3, "a", true),
            Triple(3, "a", false),
            Triple(3, "b", true),
            Triple(3, "b", false),
            Triple(3, "c", true),
            Triple(3, "c", false)
         )
      }

      test("Exhaustive.cartesian arity 3") {
         val e = Exhaustive.cartesian(
            Exhaustive.of(1, 2, 3),
            Exhaustive.of("a", "b", "c"),
            Exhaustive.of(true, false)
         ) { a, b, c -> Triple(a, b, c) }
         e.values.shouldHaveSize(18)
         e.values shouldBe listOf(
            Triple(1, "a", true),
            Triple(1, "a", false),
            Triple(1, "b", true),
            Triple(1, "b", false),
            Triple(1, "c", true),
            Triple(1, "c", false),
            Triple(2, "a", true),
            Triple(2, "a", false),
            Triple(2, "b", true),
            Triple(2, "b", false),
            Triple(2, "c", true),
            Triple(2, "c", false),
            Triple(3, "a", true),
            Triple(3, "a", false),
            Triple(3, "b", true),
            Triple(3, "b", false),
            Triple(3, "c", true),
            Triple(3, "c", false)
         )
      }

      test("Exhaustive.cartesian arity 4") {
         val e = Exhaustive.cartesian(
            Exhaustive.of(1, 2),
            Exhaustive.of("a", "b", "c"),
            Exhaustive.of(true, false),
            Exhaustive.of('p', 'q'),
         ) { a, b, c, d -> Tuple4(a, b, c, d) }
         e.values.shouldHaveSize(24)
         e.values shouldBe listOf(
            Tuple4(a = 1, b = "a", c = true, d = 'p'),
            Tuple4(a = 1, b = "a", c = true, d = 'q'),
            Tuple4(a = 1, b = "a", c = false, d = 'p'),
            Tuple4(a = 1, b = "a", c = false, d = 'q'),
            Tuple4(a = 1, b = "b", c = true, d = 'p'),
            Tuple4(a = 1, b = "b", c = true, d = 'q'),
            Tuple4(a = 1, b = "b", c = false, d = 'p'),
            Tuple4(a = 1, b = "b", c = false, d = 'q'),
            Tuple4(a = 1, b = "c", c = true, d = 'p'),
            Tuple4(a = 1, b = "c", c = true, d = 'q'),
            Tuple4(a = 1, b = "c", c = false, d = 'p'),
            Tuple4(a = 1, b = "c", c = false, d = 'q'),
            Tuple4(a = 2, b = "a", c = true, d = 'p'),
            Tuple4(a = 2, b = "a", c = true, d = 'q'),
            Tuple4(a = 2, b = "a", c = false, d = 'p'),
            Tuple4(a = 2, b = "a", c = false, d = 'q'),
            Tuple4(a = 2, b = "b", c = true, d = 'p'),
            Tuple4(a = 2, b = "b", c = true, d = 'q'),
            Tuple4(a = 2, b = "b", c = false, d = 'p'),
            Tuple4(a = 2, b = "b", c = false, d = 'q'),
            Tuple4(a = 2, b = "c", c = true, d = 'p'),
            Tuple4(a = 2, b = "c", c = true, d = 'q'),
            Tuple4(a = 2, b = "c", c = false, d = 'p'),
            Tuple4(a = 2, b = "c", c = false, d = 'q'),
         )
      }
   }
}
