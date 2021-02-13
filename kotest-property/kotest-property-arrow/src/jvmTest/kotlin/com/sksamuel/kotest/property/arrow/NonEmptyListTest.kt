package com.sksamuel.kotest.property.arrow

import arrow.core.NonEmptyList
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
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
         NonEmptyList.of("a", "a", "a", "a"),
         NonEmptyList.of("a", "a", "a", "a"),
         NonEmptyList.of("a", "a")
      )
   }

   test("Arb.nel should generate NonEmptyList") {
      val expected = listOf(
         NonEmptyList.of(1),
         NonEmptyList.of(1, 4),
         NonEmptyList.of(1, 5, 4),
         NonEmptyList.of(1, 9, 9),
         NonEmptyList.of(1, 8, 7),
         NonEmptyList.of(1, 1),
         NonEmptyList.of(1, 4, 9, 5, 6),
         NonEmptyList.of(1, 9, 5),
         NonEmptyList.of(1, 5),
         NonEmptyList.of(1, 2, 4, 6)
      )

      Arb.nel(Arb.int(1..10), 1..5)
         .take(10, RandomSource.seeded(123123L))
         .toList() shouldContainExactly expected

      Arb.nonEmptyList(Arb.int(1..10), 1..5)
         .take(10, RandomSource.seeded(123123L))
         .toList() shouldContainExactly expected
   }
})
