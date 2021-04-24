package com.sksamuel.kotest.property.arrow

import arrow.core.nonEmptyListOf
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.*
import io.kotest.property.arrow.nel
import io.kotest.property.arrow.nonEmptyList
import io.kotest.property.checkAll

class NonEmptyListTest : FunSpec({

   test("Arb.nel should not allow range less than 0") {
      checkAll(Arb.negativeInts(), Arb.positiveInts()) { start, end ->
         shouldThrowMessage("start of range must not be less than 1") {
            Arb.nel(Arb.int(), start..end)
         }
      }
   }

   test("Arb.nel should not fail if generator arb has no edgecases") {
      Arb.nel(Arb.constant("a"), 1..5)
         .take(3, RandomSource.seeded(123123L))
         .toList() shouldContainExactly listOf(
         nonEmptyListOf("a", "a"),
         nonEmptyListOf("a", "a"),
         nonEmptyListOf("a", "a", "a", "a")
      )
   }

   test("Arb.nel should generate NonEmptyList") {
      val expected = listOf(
         nonEmptyListOf(1, 4),
         nonEmptyListOf(8, 1, 5, 6),
         nonEmptyListOf(3, 1, 7, 3),
         nonEmptyListOf(3, 9, 2),
         nonEmptyListOf(4, 8),
         nonEmptyListOf(9),
         nonEmptyListOf(1, 1, 7, 3, 2),
         nonEmptyListOf(7, 1, 4),
         nonEmptyListOf(5, 4, 1, 4),
         nonEmptyListOf(6, 6, 4, 6)
      )

      Arb.nel(Arb.int(1..10), 1..5)
         .take(10, RandomSource.seeded(123123L))
         .toList() shouldContainExactly expected

      Arb.nonEmptyList(Arb.int(1..10), 1..5)
         .take(10, RandomSource.seeded(123123L))
         .toList() shouldContainExactly expected
   }
})
