package com.sksamuel.kotest.matchers.ranges

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ranges.Range
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

@OptIn(ExperimentalStdlibApi::class)
class RangeTest: WordSpec() {
   private val openOpenRange = Range.openOpen(1, 2)
   private val openClosedRange = Range.openClosed(2, 3)
   private val closedOpenRange = Range.closedOpen(3, 4)
   private val closedClosedRange = Range.closedClosed(4, 5)

   init {
       "create" should {
          "openOpen" {
             openOpenRange.toString() shouldBe "(1, 2)"
          }

          "openClosed" {
             openClosedRange.toString() shouldBe "(2, 3]"
          }

          "closedOpen" {
             closedOpenRange.toString() shouldBe "[3, 4)"
          }

          "closedClosed" {
             closedClosedRange.toString() shouldBe "[4, 5]"
          }

          "cannot create if end less than start" {
             shouldThrowAny {
                Range.closedClosed(4, 3)
             }.message shouldBe "4 cannot be after 3"
          }
       }

      "isEmpty" should {
         "false if start less than end" {
            openOpenRange.isEmpty().shouldBeFalse()
         }

         "true if start equals end and closed-closed" {
            Range.closedClosed(1, 1).isEmpty().shouldBeFalse()
         }

         "false if start equals end and not closed-closed" {
            Range.openClosed(1, 1).isEmpty().shouldBeTrue()
            Range.closedOpen(1, 1).isEmpty().shouldBeTrue()
            Range.openOpen(1, 1).isEmpty().shouldBeTrue()
         }
      }

      "lessThan" should {
            "true if gap" {
               openOpenRange.lessThan(closedOpenRange).shouldBeTrue()
               closedOpenRange.greaterThan(openOpenRange).shouldBeTrue()
            }

            "true if common edge but not both are inclusive" {
               forAll(
                  row(Range.openOpen(1, 2), Range.openOpen(2, 3), "both ends exclusive"),
                  row(Range.openClosed(1, 2), Range.openOpen(2, 3), "left inclusive, right exclusive"),
                  row(Range.openOpen(1, 2), Range.closedOpen(2, 3), "left exclusive, right inclusive"),
               ) { left, right, description ->
                  withClue(description) {
                     assertSoftly {
                        left.lessThan(right).shouldBeTrue()
                        right.greaterThan(left).shouldBeTrue()
                     }
                  }
               }
            }

         "false if common edge and both ends are inclusive" {
            Range.openClosed(1, 2).lessThan(Range.closedOpen(2, 3)).shouldBeFalse()
         }
      }

      "intersect" should {
         "work for two closed ranges" {
            forAll(
               Arb.int(1..3), Arb.int(1..3), Arb.int(0..4), Arb.int(1..2)
            ) { rangeStart, rangeLength, otherStart, otherLength ->
               val range = rangeStart..(rangeStart + rangeLength)
               val other = otherStart..(otherStart + otherLength)
               Range.ofClosedRange(range).intersect(Range.ofClosedRange(other)) == range.toSet().intersect(other.toSet()).isNotEmpty()
            }
         }

         "work for two open end ranges" {
            forAll(
               Arb.int(1..3), Arb.int(1..3), Arb.int(0..4), Arb.int(1..2)
            ) { rangeStart, rangeLength, otherStart, otherLength ->
               val range = rangeStart..<(rangeStart + rangeLength)
               val other = otherStart..<(otherStart + otherLength)
               Range.ofOpenEndRange(range).intersect(Range.ofOpenEndRange(other)) == range.toSet().intersect(other.toSet()).isNotEmpty()
            }
         }

         "work for closed range and open end one" {
            forAll(
               Arb.int(1..3), Arb.int(1..3), Arb.int(0..4), Arb.int(1..2)
            ) { rangeStart, rangeLength, otherStart, otherLength ->
               val range = rangeStart..(rangeStart + rangeLength)
               val other = otherStart..<(otherStart + otherLength)
               (Range.ofClosedRange(range).intersect(Range.ofOpenEndRange(other)) == range.toSet().intersect(other.toSet()).isNotEmpty())
                  &&
                  (Range.ofOpenEndRange(other).intersect(Range.ofClosedRange(range)) == range.toSet().intersect(other.toSet()).isNotEmpty())
            }
         }
      }
   }
}
