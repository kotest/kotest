package com.sksamuel.kotest.property.exhaustive

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.permutations
import io.kotest.property.exhaustive.sliceIndexes

//@EnabledIf(LinuxCondition::class)
class PermutationsTest : FunSpec() {
   init {
      test("Exhaustive.permutations should generate full permutations when length is omitted") {
         Exhaustive.permutations(listOf(1, 2, 3)).values shouldContainExactlyInAnyOrder listOf(
            listOf(3, 2, 1), listOf(2, 3, 1), listOf(3, 1, 2), listOf(1, 3, 2), listOf(2, 1, 3), listOf(1, 2, 3)
         )
      }

      test("Exhaustive.permutations should generate partial permutations of given length") {
         Exhaustive.permutations(listOf(1, 2, 3), 2).values shouldContainExactlyInAnyOrder listOf(
            listOf(1, 2), listOf(1, 3), listOf(2, 1), listOf(2, 3), listOf(3, 1), listOf(3, 2)
         )
      }

      test("Exhaustive.permutations should generate a single empty list when length is zero") {
         Exhaustive.permutations(listOf(1, 2, 3), 0).values shouldBe listOf(listOf())
      }

      test("Exhaustive.permutations should throw if length is negative") {
         shouldThrowWithMessage<IllegalArgumentException>("length must be between 0 and the list size (1), but was -1.")
         { Exhaustive.permutations(listOf(1), -1) }
      }

      test("Exhaustive.permutations should throw if length is greater than list length") {
         shouldThrowWithMessage<IllegalArgumentException>("length must be between 0 and the list size (3), but was 7.")
         { Exhaustive.permutations(listOf(1, 2, 3), 7) }
      }

      test("sliceIndexes for 1") {
         sliceIndexes(1).toList() shouldContainExactlyInAnyOrder
            listOf(
               Pair(0, 0),
            )
      }

      test("sliceIndexes for 2") {
         sliceIndexes(2).toList() shouldContainExactlyInAnyOrder
            listOf(
               Pair(0, 0),
               Pair(1, 1),
               Pair(0, 1),
            )
      }

      test("sliceIndexes for 3") {
         sliceIndexes(3).toList() shouldContainExactlyInAnyOrder
            listOf(
               Pair(0, 0),
               Pair(1, 1),
               Pair(2, 2),
               Pair(0, 1),
               Pair(1, 2),
               Pair(0, 2),
            )
      }
   }
}
