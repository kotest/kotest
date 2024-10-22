package io.kotest.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class CondenseTest: StringSpec() {
   init {
      "condenseToRanges handles empty list" {
         val indexes = emptyList<Int>()
         val condensed = indexes.condenseToRanges(2).toList()
         condensed.shouldBeEmpty()
      }
      "condenseToRanges handles one element" {
         val indexes = listOf(0)
         val condensed = indexes.condenseToRanges(2).toList()
         condensed shouldBe listOf(IndexesToPrint.SingleIndex(0))
      }
      "condenseToRanges handles consecutive indexes, less than`minRangeSize" {
         val indexes = listOf(0, 1)
         val condensed = indexes.condenseToRanges(3).toList()
         condensed shouldBe listOf(IndexesToPrint.SingleIndex(0), IndexesToPrint.SingleIndex(1),)
      }
      "condenseToRanges handles consecutive indexes of`minRangeSize" {
         val indexes = listOf(0, 1, 2)
         val condensed = indexes.condenseToRanges(3).toList()
         condensed shouldBe listOf(IndexesToPrint.RangeOfIndexes(0, 2),)
      }
      "condenseToRanges handles non-consecutive indexes" {
         val indexes = listOf(0, 1, 3)
         val condensed = indexes.condenseToRanges(3).toList()
         condensed shouldBe listOf(
            IndexesToPrint.SingleIndex(0),
            IndexesToPrint.SingleIndex(1),
            IndexesToPrint.SingleIndex(3),
         )
      }
      "condenseToRanges collapses consecutive indexes into ranges" {
         val indexes = listOf(1, 2, 3, 4, 5, 7, 10, 11, 12, 13, 14, 20, 21, 22, 23, 24, 42)
         val condensed = indexes.condenseToRanges(2).toList()
         condensed shouldContainExactly listOf(
            IndexesToPrint.RangeOfIndexes(1, 5),
            IndexesToPrint.SingleIndex(7),
            IndexesToPrint.RangeOfIndexes(10, 14),
            IndexesToPrint.RangeOfIndexes(20, 24),
            IndexesToPrint.SingleIndex(42)
         )
      }
   }
}
