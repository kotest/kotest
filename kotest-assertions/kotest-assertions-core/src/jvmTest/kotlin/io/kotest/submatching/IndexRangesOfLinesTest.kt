package io.kotest.submatching

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class IndexRangesOfLinesTest: StringSpec() {
   init {
      "handle single line" {
         val line = "The quick brown fox jumps over the lazy dog"
         indexRangesOfLines(line).toList() shouldBe listOf(IndexRange(0, line.length - 1))
      }
      "empty line" {
         indexRangesOfLines("").toList() shouldBe listOf()
      }
      "no non empty lines" {
         indexRangesOfLines("\r\n\n").toList() shouldBe listOf()
      }
      "handle two lines with one separator" {
         indexRangesOfLines("The quick brown fox\njumps over the lazy dog").toList() shouldBe listOf(
            IndexRange(0, 18),
            IndexRange(20, 42)
         )
      }
      "handle two lines with multiple separators" {
         indexRangesOfLines("The quick brown fox\n\njumps over the lazy dog").toList() shouldBe listOf(
            IndexRange(0, 18),
            IndexRange(21, 43)
         )
      }
      "handle two lines with one separator in the middle and one at the end" {
         indexRangesOfLines("The quick brown fox\njumps over the lazy dog\n").toList() shouldBe listOf(
            IndexRange(0, 18),
            IndexRange(20, 42)
         )
      }
   }
}
