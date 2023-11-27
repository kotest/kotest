package com.sksamuel.kotest.matchers.ranges

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ranges.shouldIntersect
import io.kotest.matchers.ranges.shouldNotIntersect
import io.kotest.matchers.shouldBe

class ClosedIntersectClosedTest: WordSpec() {
   private val oneThree: ClosedRange<Int> = (1..3)
   private val twoFour: ClosedRange<Int> = (2..4)
   private val threeFour: ClosedRange<Int> = (3..4)
   private val threeFive: ClosedRange<Int> = (3..5)
   private val fourSix: ClosedRange<Int> = (4..6)
   init {
      "should" should {
         "fail if left below right" {
             shouldThrowAny {
                oneThree shouldIntersect fourSix
             }.message shouldBe "Range [1, 3] should intersect [4, 6], but doesn't"
         }

         "fail if right below left" {
            shouldThrowAny {
               fourSix shouldIntersect oneThree
            }.message shouldBe "Range [4, 6] should intersect [1, 3], but doesn't"
         }

         "pass if have common edge" {
            shouldNotThrowAny {
               oneThree shouldIntersect threeFive
               threeFive shouldIntersect oneThree
            }
         }

         "pass if intersect but not completely inside one another" {
            shouldNotThrowAny {
               oneThree shouldIntersect twoFour
               twoFour shouldIntersect oneThree
            }
         }

         "pass if one completely inside another" {
            shouldNotThrowAny {
               twoFour shouldIntersect threeFour
               threeFour shouldIntersect twoFour
            }
         }
      }

      "shouldNot" should {
         "pass if left below right" {
            shouldNotThrowAny {
               oneThree shouldNotIntersect fourSix
            }
         }

         "pass if right below left" {
            shouldNotThrowAny {
               fourSix shouldNotIntersect oneThree
            }
         }

         "fail if have common edge" {
            shouldThrowAny {
               oneThree shouldNotIntersect threeFive
            }.message shouldBe "Range [1, 3] should not intersect [3, 5], but does"

            shouldThrowAny {
               threeFive shouldNotIntersect oneThree
            }.message shouldBe "Range [3, 5] should not intersect [1, 3], but does"
         }

         "fail if intersect but not completely inside one another" {
            shouldThrowAny {
               oneThree shouldNotIntersect twoFour
            }.message shouldBe "Range [1, 3] should not intersect [2, 4], but does"

            shouldThrowAny {
               twoFour shouldNotIntersect oneThree
            }.message shouldBe "Range [2, 4] should not intersect [1, 3], but does"
         }

         "fail if one completely inside another" {
            shouldThrowAny {
               twoFour shouldNotIntersect threeFour
            }.message shouldBe "Range [2, 4] should not intersect [3, 4], but does"

            shouldThrowAny {
               threeFour shouldNotIntersect twoFour
            }.message shouldBe "Range [3, 4] should not intersect [2, 4], but does"
         }
      }
   }
}
