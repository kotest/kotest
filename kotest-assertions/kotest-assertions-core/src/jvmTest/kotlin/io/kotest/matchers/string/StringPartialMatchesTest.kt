package io.kotest.matchers.string

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.submatching.MatchedCollectionElement
import io.kotest.submatching.PartialCollectionMatch
import io.kotest.submatching.PartialMatchesInCollectionDescription
import io.kotest.submatching.describePartialMatchesInString
import io.kotest.submatching.describePartialMatchesInStringForPrefix
import io.kotest.submatching.describePartialMatchesInStringForSlice
import io.kotest.submatching.describePartialMatchesInStringForSuffix
import io.kotest.submatching.underscoreSubstring

@EnabledIf(LinuxCondition::class)
class StringPartialMatchesTest : WordSpec() {
   val value = "0123456789"
   val text = """The quick brown fox
            |jumps over
            |the lazy dog""".trimMargin()
   val line = "The quick brown fox jumps over the lazy dog"

   init {
      "underscoreSubstring" should {
         "underscore start" {
            underscoreSubstring(10, 0, 3) shouldBe "++++------"
         }
         "underscore middle" {
            underscoreSubstring(10, 3, 7) shouldBe "---+++++--"
         }
         "underscore end" {
            underscoreSubstring(10, 7, 10) shouldBe "-------+++"
         }
      }
      "describePartialMatchesInString" should {
         "return empty if no matches" {
            describePartialMatchesInStringForSlice("hawk", text) shouldBe PartialMatchesInCollectionDescription.Empty
         }
         "handle empty slice" {
            val actual = describePartialMatchesInStringForSlice("", text)
            actual.partialMatchesList shouldBe ""
            actual.partialMatchesDescription shouldBe ""
         }
         "handle empty text" {
            val actual = describePartialMatchesInStringForSlice("something", "")
            actual.partialMatchesList shouldBe ""
            actual.partialMatchesDescription shouldBe ""
         }
         "find one match in one line" {
            val actual = describePartialMatchesInStringForSlice("brown fox jumps over", line)
            actual.partialMatchesList shouldBe "Match[0]: whole slice matched actual[10..29]"
            actual.partialMatchesDescription shouldBe
               """Line[0] ="The quick brown fox jumps over the lazy dog"
                 |Match[0]= ----------++++++++++++++++++++-------------""".trimMargin()
            actual.partialMatches shouldBe listOf(
               PartialCollectionMatch(
                  matchedElement= MatchedCollectionElement(startIndexInExpected=0, startIndexInValue=10),
                  length=20
               )
            )
         }
         "find one match spanning two lines" {
            val twoLines = "The quick brown fox\n jumps over the lazy dog"
            val actual = describePartialMatchesInStringForSlice("brown fox\n jumps over", twoLines)
            actual.partialMatchesList.shouldContainInOrder(
               "Match[0]: whole slice matched actual[10..30]",
            )
            actual.partialMatchesDescription.lines() shouldBe
               listOf(
                  "Line[0] =\"The quick brown fox\"",
                  "Match[0]= ----------+++++++++",
                  "Line[1] =\" jumps over the lazy dog\"",
                  "Match[0]= +++++++++++-------------"
               )
            actual.partialMatches shouldBe listOf(
               PartialCollectionMatch(
                  matchedElement= MatchedCollectionElement(startIndexInExpected=0, startIndexInValue=10),
                  length=21
               )
            )
         }
         "find two matches on separate lines" {
            val twoLines = "The quick brown fox\n jumps over the lazy dog"
            val actual = describePartialMatchesInStringForSlice("Rabbit jumps over brown fox", twoLines)
            actual.partialMatchesList.shouldContainInOrder(
               "Match[0]: part of slice with indexes [6..17] matched actual[20..31]",
               "Match[1]: part of slice with indexes [17..26] matched actual[9..18]"
            )
            actual.partialMatchesDescription.lines() shouldBe
               listOf(
                  "Line[0] =\"The quick brown fox\"",
                  "Match[1]= ---------++++++++++",
                  "Line[1] =\" jumps over the lazy dog\"",
                  "Match[0]= ++++++++++++------------",
               )
            actual.partialMatches shouldBe listOf(
               PartialCollectionMatch(
                  matchedElement= MatchedCollectionElement(startIndexInExpected=6, startIndexInValue=20),
                  length=12
               ),
               PartialCollectionMatch(
                  matchedElement= MatchedCollectionElement(startIndexInExpected=17, startIndexInValue=9),
                  length=10
               ),
            )
         }
         "find match that takes one whole line" {
            val threeLines = "What?\nThe quick brown fox jumps over the lazy dog.\nAnd that's it."
            val actual = describePartialMatchesInStringForSlice(line, threeLines)
            actual.partialMatchesDescription.lines() shouldBe listOf(
               "Line[0] =\"What?\"",
               "Line[1] =\"The quick brown fox jumps over the lazy dog.\"",
               "Match[0]= +++++++++++++++++++++++++++++++++++++++++++-",
               "Line[2] =\"And that's it.\"",
            )
            actual.partialMatches shouldBe listOf(
               PartialCollectionMatch(
                  matchedElement= MatchedCollectionElement(startIndexInExpected=0, startIndexInValue=6),
                  length=43
               )
            )
         }
         "find whole prefix elsewhere" {
            val actual = describePartialMatchesInStringForPrefix("quick brown fox jumps", line)
            actual.partialMatchesList shouldBe "Match[0]: whole prefix matched actual[4..24]"
            actual.partialMatchesDescription.lines() shouldBe
               listOf(
                 """Line[0] ="The quick brown fox jumps over the lazy dog"""",
                  """Match[0]= ----+++++++++++++++++++++------------------"""
               )
            actual.partialMatches shouldBe listOf(
               PartialCollectionMatch(
                  matchedElement= MatchedCollectionElement(startIndexInExpected=0, startIndexInValue=4),
                  length=21
               )
            )
         }
         "find partial prefix elsewhere" {
            val actual = describePartialMatchesInStringForPrefix("quick brown fox runs", line)
            actual.partialMatchesList shouldBe "Match[0]: part of prefix with indexes [0..15] matched actual[4..19]"
            actual.partialMatchesDescription.lines() shouldBe
               listOf(
                  """Line[0] ="The quick brown fox jumps over the lazy dog"""",
                  """Match[0]= ----++++++++++++++++-----------------------"""
               )
            actual.partialMatches shouldBe listOf(
               PartialCollectionMatch(
                  matchedElement= MatchedCollectionElement(startIndexInExpected=0, startIndexInValue=4),
                  length=16
               )
            )
         }
         "find whole suffix elsewhere" {
            val actual = describePartialMatchesInStringForSuffix("jumps over the lazy", line)
            actual.partialMatchesList shouldBe "Match[0]: whole suffix matched actual[20..38]"
            actual.partialMatchesDescription.lines() shouldBe
               listOf(
                  """Line[0] ="The quick brown fox jumps over the lazy dog"""",
                  """Match[0]= --------------------+++++++++++++++++++----"""
               )
            actual.partialMatches shouldBe listOf(
               PartialCollectionMatch(
                  matchedElement= MatchedCollectionElement(startIndexInExpected=0, startIndexInValue=20),
                  length=19
               )
            )
         }
         "find partial suffix elsewhere" {
            val actual = describePartialMatchesInStringForSuffix("jumps over the lazy cat", line)
            actual.partialMatchesList shouldBe "Match[0]: part of suffix with indexes [0..19] matched actual[20..39]"
            actual.partialMatchesDescription.lines() shouldBe
               listOf(
                  """Line[0] ="The quick brown fox jumps over the lazy dog"""",
                  """Match[0]= --------------------++++++++++++++++++++---"""
               )
            actual.partialMatches shouldBe listOf(
               PartialCollectionMatch(
                  matchedElement= MatchedCollectionElement(startIndexInExpected=0, startIndexInValue=20),
                  length=20
               )
            )
         }
      }
   }
}
