package com.sksamuel.kotest.matchers.string

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.conflateSubMatchWithMapping
import io.kotest.matchers.string.describeMatchedSubstrings
import io.kotest.matchers.string.prefixIfNotEmpty
import io.kotest.matchers.string.rangeWithMargin
import io.kotest.matchers.string.rangeWithMarginMapping
import io.kotest.matchers.string.submatchWithMarginMapping
import io.kotest.submatching.MatchedCollectionElement
import io.kotest.submatching.PartialCollectionMatch

class SubstringMatchersTest: WordSpec() {
   private val value = "The quick brown fox jumps over the lazy dog"
   init {
      "submatchWithMarginMapping" should {
         submatchWithMarginMapping(
            value,
            PartialCollectionMatch(MatchedCollectionElement(0, 20), 5, "jumps".toList())
         ) shouldBe listOf(
            ">>>>>+++++<<<<<",
            " fox jumps over"
         ).joinToString("\n")
      }
      "conflateSubMatchWithMapping" should {
         "work for submatch that is on one line" {
            val submatchWithMargin = "The quick brown fox jumps over the lazy dog"
            val rangeWithMarginMapping = ">>>>>>>++++++++<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
            conflateSubMatchWithMapping(
               submatchWithMargin,
               rangeWithMarginMapping
            ) shouldBe "$rangeWithMarginMapping\n$submatchWithMargin"
         }
         "work for submatch that spans two lines" {
            val submatchWithMargin     = "The quick brown fox\njumps over the lazy dog"
            val rangeWithMarginMapping = ">>>>>>>++++++++<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
            val actual = conflateSubMatchWithMapping(
               submatchWithMargin,
               rangeWithMarginMapping
            )
            println(actual)
            actual shouldBe ">>>>>>>++++++++<<<<\nThe quick brown fox\n<<<<<<<<<<<<<<<<<<<<<<<\njumps over the lazy dog"
         }
      }
      "describeMatchedSubstrings" should {
         "handle empty list of matches" {
            describeMatchedSubstrings("apple", listOf()) shouldBe ""
         }
         "work with several matches" {
            describeMatchedSubstrings("workload", listOf(
               PartialCollectionMatch(
                  MatchedCollectionElement(indexInValue = 0, indexInTarget = 5),
                  length = 4,
                  value = "workload".toList()
               ),
               PartialCollectionMatch(
                  MatchedCollectionElement(indexInValue = 4, indexInTarget = 0),
                  length = 4,
                  value = "workload".toList()
               )
            )) shouldBe "Substring <\"work\"> at indexes [0..3] matches at indexes [5..8]\nSubstring <\"load\"> at indexes [4..7] matches at indexes [0..3]"
         }
      }
      "rangeWithMargin" should {
         "work at the very start" {
            rangeWithMargin(0..5, margin = 3, maxIndex = 20) shouldBe 0..8
         }
         "work at less than margin from start" {
            rangeWithMargin(2..5, margin = 3, maxIndex = 20) shouldBe 0..8
         }
         "work at exactly margin from start" {
            rangeWithMargin(3..5, margin = 3, maxIndex = 20) shouldBe 0..8
         }
         "work at more than margin from start and end" {
            rangeWithMargin(3..5, margin = 2, maxIndex = 20) shouldBe 1..7
         }
         "work at exactly margin from end" {
            rangeWithMargin(12..17, margin = 3, maxIndex = 20) shouldBe 9..20
         }
         "work at less than margin from end" {
            rangeWithMargin(12..18, margin = 3, maxIndex = 20) shouldBe 9..20
         }
         "work at the very end" {
            rangeWithMargin(12..20, margin = 3, maxIndex = 20) shouldBe 9..20
         }
      }
      "rangeWithMarginMapping" should {
         "work at the start" {
            rangeWithMarginMapping(0..5,  0..8) shouldBe "++++++<<<"
         }
         "work in the middle" {
            rangeWithMarginMapping(2..5,  0..8) shouldBe ">>++++<<<"
         }
         "work at the end" {
            rangeWithMarginMapping(3..8,  0..8) shouldBe ">>>++++++"
         }
      }
      "prefixIfNotEmpty" should {
         "handle empty string" {
            prefixIfNotEmpty("") { "Prefix: " } shouldBe ""
         }
         "handle non-empty string" {
            prefixIfNotEmpty("apple") { "Prefix: " } shouldBe "Prefix: apple"
         }
      }
   }
}
