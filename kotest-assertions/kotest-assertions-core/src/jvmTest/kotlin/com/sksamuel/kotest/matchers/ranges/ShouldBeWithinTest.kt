package com.sksamuel.kotest.matchers.ranges

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ranges.shouldBeWithin
import io.kotest.matchers.ranges.shouldNotBeWithin
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalStdlibApi::class)
class ShouldBeWithinTest : WordSpec() {
   private val smallClosedRange: ClosedRange<Int> = 1..3
   private val smallOpenEndRange: OpenEndRange<Int> = 1 until 3
   private val bigClosedRange: ClosedRange<Int> = 0..4
   private val bigOpenEndRange: OpenEndRange<Int> = 0 until 4

   init {
      "shouldBeWithin" should {
         "fail" {
               shouldThrowAny {
                  bigClosedRange shouldBeWithin smallClosedRange
               }.message shouldBe "Range [0, 4] should be within [1, 3], but it isn't"
               shouldThrowAny {
                  bigClosedRange shouldBeWithin smallOpenEndRange
               }.message shouldBe "Range [0, 4] should be within [1, 3), but it isn't"
               shouldThrowAny {
                  bigOpenEndRange shouldBeWithin smallClosedRange
               }.message shouldBe "Range [0, 4) should be within [1, 3], but it isn't"
               shouldThrowAny {
                  bigOpenEndRange shouldBeWithin smallOpenEndRange
               }.message shouldBe "Range [0, 4) should be within [1, 3), but it isn't"
         }

         "succeed" {
            assertSoftly {
               smallClosedRange shouldBeWithin bigClosedRange
               smallClosedRange shouldBeWithin bigOpenEndRange
               smallOpenEndRange shouldBeWithin bigClosedRange
               smallOpenEndRange shouldBeWithin bigOpenEndRange
            }
         }

         "handle edge case of OpenEndRange inside ClosedRange for Int" {
            val closedRange: ClosedRange<Int> = 1..2
            val openEndRange: OpenEndRange<Int> = 1 until 3
            openEndRange shouldBeWithin closedRange
         }

         "handle edge case of OpenEndRange inside ClosedRange for Long" {
            val closedRange: ClosedRange<Long> = 1L..2L
            val openEndRange: OpenEndRange<Long> = 1L until 3L
            openEndRange shouldBeWithin closedRange
         }
       }

      "shouldNotBeWithin" should {
         "fail" {
            shouldThrowAny {
               smallClosedRange shouldNotBeWithin bigClosedRange
            }.message shouldBe "Range [1, 3] should not be within [0, 4], but it is"
            shouldThrowAny {
               smallClosedRange shouldNotBeWithin bigOpenEndRange
            }.message shouldBe "Range [1, 3] should not be within [0, 4), but it is"
            shouldThrowAny {
               smallOpenEndRange shouldNotBeWithin bigClosedRange
            }.message shouldBe "Range [1, 3) should not be within [0, 4], but it is"
            shouldThrowAny {
               smallOpenEndRange shouldNotBeWithin bigOpenEndRange
            }.message shouldBe "Range [1, 3) should not be within [0, 4), but it is"
         }

         "fail for edge case of OpenEndRange inside ClosedRange for Int" {
            val closedRange: ClosedRange<Int> = 1..2
            val openEndRange: OpenEndRange<Int> = 1 until 3
            shouldThrowAny {
               openEndRange shouldNotBeWithin closedRange
            }.message shouldBe "Range [1, 3) should not be within [1, 2], but it is"
         }

         "fail for edge case of OpenEndRange inside ClosedRange for Long" {
            val closedRange: ClosedRange<Long> = 1L..2L
            val openEndRange: OpenEndRange<Long> = 1L until 3L
            shouldThrowAny {
               openEndRange shouldNotBeWithin closedRange
            }.message shouldBe "Range [1, 3) should not be within [1, 2], but it is"
         }

         "succeed" {
            assertSoftly {
               bigClosedRange shouldNotBeWithin smallClosedRange
               bigClosedRange shouldNotBeWithin smallOpenEndRange
               bigOpenEndRange shouldNotBeWithin smallClosedRange
               bigOpenEndRange shouldNotBeWithin smallOpenEndRange
            }
         }
      }
   }
}
