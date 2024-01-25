package com.sksamuel.kotest.matchers.ranges

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ranges.shouldIntersect
import io.kotest.matchers.ranges.shouldNotIntersect
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalStdlibApi::class)
class ClosedIntersectOpenEndTest: WordSpec() {
   private val oneThree: ClosedRange<Double> = (1.0..3.0)
   private val twoFour: ClosedRange<Double> = (2.0 .. 4.0)
   private val fourSix: ClosedRange<Double> = (4.0..6.0)
   init {
      "should" should {
         "fail" {
             shouldThrowAny {
                val openEndRange = oneThree.toOpenEndRange()
                println(openEndRange)
                openEndRange shouldIntersect fourSix
             }.message shouldBe "Range [1.0, 3.0) should intersect [4.0, 6.0], but doesn't"
         }

         "pass" {
            shouldNotThrowAny {
               oneThree.toOpenEndRange() shouldIntersect twoFour
            }
         }
      }

      "shouldNot" should {
         "pass" {
            shouldNotThrowAny {
               oneThree.toOpenEndRange() shouldNotIntersect fourSix
            }
         }

         "fail" {
            shouldThrowAny {
               oneThree.toOpenEndRange() shouldNotIntersect twoFour
            }.message shouldBe "Range [1.0, 3.0) should not intersect [2.0, 4.0], but does"
         }
      }
   }

   private fun ClosedRange<Double>.toOpenEndRange() = this.start.rangeUntil(this.endInclusive)
}

