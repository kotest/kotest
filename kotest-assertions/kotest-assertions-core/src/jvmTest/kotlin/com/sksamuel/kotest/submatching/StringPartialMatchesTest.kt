package io.kotest.submatching

import io.kotest.assertions.submatching.PartialMatchType
import io.kotest.assertions.submatching.describePartialMatchesInString
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldNotContain

class StringPartialMatchesTest: WordSpec() {
   init {
       "describePartialMatchesInString" should {
          "work for no matches at all" {
             val actual = describePartialMatchesInString(
                "roses are red violets are blue",
                "apples are green bananas are yellow",
                PartialMatchType.Slice
             )
             actual.partialMatchesDescription shouldBe ""
          }
          "work for match and value on one line" {
             val actual = describePartialMatchesInString(
                "roses are red violets are blue",
                "Roses are red violets are blueish",
                PartialMatchType.Slice
             )
             actual.partialMatchesDescription.shouldContainInOrder(
                """Line[0] ="Roses are red violets are blueish"""",
                """Match[0]= -+++++++++++++++++++++++++++++---"""
             )
          }

          "work for match on one line, value on two lines" {
             val actual = describePartialMatchesInString(
                "roses are red violets are blue",
                "Roses are red violets are blue\nyes!",
                PartialMatchType.Slice
             )
             actual.partialMatchesDescription.shouldContainInOrder(
                """Line[0] ="Roses are red violets are blue"""",
                """Match[0]= -+++++++++++++++++++++++++++++""",
                """Line[1] ="yes!""""
             )
             actual.partialMatchesDescription shouldNotContain "Match[1]"
          }
          "work for matches spanning two lines" {
             val line0 = "roses are red violets are blue"
             val line1 = "apples are green bananas are yellow"
             val actual = describePartialMatchesInString(
                "$line0\n$line1",
                "$line1\n$line0",
                PartialMatchType.Slice
             )
             actual.partialMatchesList shouldBe
                "Match[0]: part of slice with indexes [0..29] matched actual[36..65]\n" +
                "Match[1]: part of slice with indexes [31..65] matched actual[0..34]"
             actual.partialMatchesDescription.shouldContainInOrder(
                """Line[0] ="apples are green bananas are yellow"""",
                """Match[1]= +++++++++++++++++++++++++++++++++++""",
                """Line[1] ="roses are red violets are blue"""",
                """Match[0]= ++++++++++++++++++++++++++++++""",
             )
             actual.partialMatchesDescription shouldNotContain "Match[0]= --"
             actual.partialMatchesDescription shouldNotContain "Match[1]= --"
          }
       }
   }
}
