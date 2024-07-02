package com.sksamuel.kotest.matchers.ranges

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ranges.shouldIntersect
import io.kotest.matchers.ranges.shouldNotIntersect
import io.kotest.matchers.shouldBe

class OpenEndIntersectOpenEndTest : WordSpec() {
   private val oneThree: OpenEndRange<Double> = 1.0.rangeUntil(3.0)
   private val twoFour: OpenEndRange<Double> = 2.0.rangeUntil(4.0)
   private val threeFour: OpenEndRange<Double> = 3.0.rangeUntil(4.0)
   private val fourSix: OpenEndRange<Double> = 4.0.rangeUntil(6.0)

   init {
      "should" should {
         "fail" {
            shouldThrowAny {
               val openEndRange = oneThree
               println(openEndRange)
               openEndRange shouldIntersect fourSix
            }.message shouldBe "Range [1.0, 3.0) should intersect [4.0, 6.0), but doesn't"
         }

         "pass" {
            shouldNotThrowAny {
               oneThree shouldIntersect twoFour
            }
         }
      }

      "shouldNot" should {
         "pass" {
            shouldNotThrowAny {
               oneThree shouldNotIntersect threeFour
            }
         }

         "fail" {
            shouldThrowAny {
               oneThree shouldNotIntersect twoFour
            }.message shouldBe "Range [1.0, 3.0) should not intersect [2.0, 4.0), but does"
         }
      }
   }
}
