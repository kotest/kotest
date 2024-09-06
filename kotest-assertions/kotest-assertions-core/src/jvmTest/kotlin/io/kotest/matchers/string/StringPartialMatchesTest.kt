package io.kotest.matchers.string

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.submatching.PartialMatchesInCollectionDescription
import io.kotest.submatching.describePartialMatchesInString
import io.kotest.submatching.splitUnderscoreToFitLines
import io.kotest.submatching.underscoreSubstring

class StringPartialMatchesTest: WordSpec() {
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
      "splitUnderscoreToFitLines" should {
         "work with one line" {
            val underscoredLine = "---++++---"
            splitUnderscoreToFitLines(
               lines = listOf(value),
               underscoredLine = underscoredLine
            ) shouldBe listOf(underscoredLine)
         }
         "split into two lines" {
            val underscoredLine = "---++++---"
            val anotherUnderscoredLine = "-++++-----"
            splitUnderscoreToFitLines(
               lines = listOf(value, value),
               underscoredLine = "$underscoredLine-$anotherUnderscoredLine"
            ) shouldBe listOf(underscoredLine, anotherUnderscoredLine)
         }
      }
      "describePartialMatchesInString" should {
         "return empty if no matches" {
            describePartialMatchesInString("hawk", text) shouldBe PartialMatchesInCollectionDescription("", "")
         }
         "find one match in one line" {
            val actual = describePartialMatchesInString("brown fox jumps over", line)
            actual.partialMatchesList shouldBe "Match[0]: expected[0..19] matched actual[10..29]"
            actual.partialMatchesDescription shouldBe
               """Line[0] ="The quick brown fox jumps over the lazy dog"
                 |Match[0]= ----------++++++++++++++++++++-------------""".trimMargin()
         }
         "find one match spanning two lines" {
            val twoLines = "The quick brown fox\n jumps over the lazy dog"
            val actual = describePartialMatchesInString("brown fox\n jumps over", twoLines)
            actual.partialMatchesList.shouldContainInOrder(
               "Match[0]: expected[0..20] matched actual[10..30]",
            )
            actual.partialMatchesDescription.lines() shouldBe
               listOf(
                  "Line[0] =\"The quick brown fox\"",
                  "Match[0]= ----------+++++++++",
                  "Line[1] =\" jumps over the lazy dog\"",
                  "Match[0]= +++++++++++-------------"
               )
         }
         "find two matches on separate lines" {
            val twoLines = "The quick brown fox\n jumps over the lazy dog"
            val actual = describePartialMatchesInString("Rabbit jumps over brown fox", twoLines)
            actual.partialMatchesList.shouldContainInOrder(
               "Match[0]: expected[6..17] matched actual[20..31]",
               "Match[1]: expected[17..26] matched actual[9..18]"
            )
            actual.partialMatchesDescription.lines() shouldBe
               listOf(
                  "Line[0] =\"The quick brown fox\"",
                  "Match[0]= -------------------",
                  "Match[1]= ---------++++++++++",
                  "Line[1] =\" jumps over the lazy dog\"",
                  "Match[0]= ++++++++++++------------",
                  "Match[1]= ------------------------"
            )
         }
         "find match that takes one whole line" {
            val threeLines = "What?\nThe quick brown fox jumps over the lazy dog.\nAnd that's it."
            val actual = describePartialMatchesInString(line, threeLines)
            actual.partialMatchesDescription.lines() shouldBe listOf(
               "Line[0] =\"What?\"",
               "Match[0]= -----",
               "Line[1] =\"The quick brown fox jumps over the lazy dog.\"",
               "Match[0]= +++++++++++++++++++++++++++++++++++++++++++-",
               "Line[2] =\"And that's it.\"",
               "Match[0]= --------------"
            )
         }
      }
   }
}
