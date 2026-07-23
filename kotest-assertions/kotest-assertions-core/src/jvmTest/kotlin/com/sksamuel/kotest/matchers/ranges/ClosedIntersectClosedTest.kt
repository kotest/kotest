package com.sksamuel.kotest.matchers.ranges

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ranges.shouldIntersect
import io.kotest.matchers.ranges.shouldNotIntersect
import io.kotest.matchers.shouldBe

class ClosedIntersectClosedTest : WordSpec() {
   private val oneThree: ClosedRange<Int> = (1..3)
   private val threeFive: ClosedRange<Int> = (3..5)
   private val fourSix: ClosedRange<Int> = (4..6)

   init {
      "should" should {
         "fail because less than other" {
            shouldThrowAny {
               oneThree shouldIntersect fourSix
            }.message shouldBe "Range [1, 3] should intersect [4, 6], but doesn't, it was less than other"
         }

         "fail because greater than other" {
            shouldThrowAny {
               fourSix shouldIntersect oneThree
            }.message shouldBe "Range [4, 6] should intersect [1, 3], but doesn't, it was greater than other"
         }

         "pass" {
            shouldNotThrowAny {
               oneThree shouldIntersect threeFive
            }
         }
      }

      "shouldNot" should {
         "pass" {
            shouldNotThrowAny {
               oneThree shouldNotIntersect fourSix
            }
         }

         "fail" {
            shouldThrowAny {
               oneThree shouldNotIntersect threeFive
            }.message shouldBe "Range [1, 3] should not intersect [3, 5], but does"
         }
      }
   }
}
