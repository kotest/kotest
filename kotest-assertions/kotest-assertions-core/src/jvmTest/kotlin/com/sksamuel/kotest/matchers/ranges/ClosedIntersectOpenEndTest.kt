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
   private val threeFour: ClosedRange<Double> = (3.0..4.0)
   private val threeFive: ClosedRange<Double> = (3.0..5.0)
   private val fourSix: ClosedRange<Double> = (4.0..6.0)
   init {
      "should" should {
         "fail if left below right" {
             shouldThrowAny {
                val openEndRange = oneThree.toOpenEndRange()
                println(openEndRange)
                openEndRange shouldIntersect fourSix
             }.message shouldBe "Range [1.0, 3.0) should intersect [4.0, 6.0], but doesn't"
         }

         "fail if right below left" {
            shouldThrowAny {
               fourSix shouldIntersect (oneThree.toOpenEndRange())
            }.message shouldBe "Range [4.0, 6.0] should intersect [1.0, 3.0), but doesn't"
         }

         "fail if have common edge, but only one inclusive" {
            val openEndRange = oneThree.toOpenEndRange()
            shouldThrowAny {
               openEndRange shouldIntersect threeFive
            }.message shouldBe "Range [1.0, 3.0) should intersect [3.0, 5.0], but doesn't"
            shouldThrowAny {
               threeFive shouldIntersect openEndRange
            }.message shouldBe "Range [3.0, 5.0] should intersect [1.0, 3.0), but doesn't"
         }

         "pass if intersect but not completely inside one another" {
            shouldNotThrowAny {
               oneThree.toOpenEndRange() shouldIntersect twoFour
               twoFour shouldIntersect oneThree.toOpenEndRange()
            }
         }

         "pass if one completely inside another" {
            shouldNotThrowAny {
               twoFour shouldIntersect threeFour.toOpenEndRange()
               threeFour.toOpenEndRange() shouldIntersect twoFour
            }
         }
      }

      "shouldNot" should {
         "pass if left below right" {
            shouldNotThrowAny {
               oneThree.toOpenEndRange() shouldNotIntersect fourSix
               oneThree.toOpenEndRange() shouldNotIntersect threeFour
            }
         }

         "pass if right below left" {
            shouldNotThrowAny {
               fourSix shouldNotIntersect oneThree.toOpenEndRange()
            }
         }

         "pass if have common edge" {
            shouldNotThrowAny {
               oneThree.toOpenEndRange() shouldNotIntersect threeFive
               threeFive shouldNotIntersect oneThree.toOpenEndRange()
            }
         }

         "fail if intersect but not completely inside one another" {
            shouldThrowAny {
               oneThree.toOpenEndRange() shouldNotIntersect twoFour
            }.message shouldBe "Range [1.0, 3.0) should not intersect [2.0, 4.0], but does"

            shouldThrowAny {
               twoFour shouldNotIntersect oneThree.toOpenEndRange()
            }.message shouldBe "Range [2.0, 4.0] should not intersect [1.0, 3.0), but does"
         }

         "fail if one completely inside another" {
            shouldThrowAny {
               twoFour shouldNotIntersect threeFour.toOpenEndRange()
            }.message shouldBe "Range [2.0, 4.0] should not intersect [3.0, 4.0), but does"

            shouldThrowAny {
               threeFour.toOpenEndRange() shouldNotIntersect twoFour
            }.message shouldBe "Range [3.0, 4.0) should not intersect [2.0, 4.0], but does"
         }
      }
   }

   private fun ClosedRange<Double>.toOpenEndRange() = this.start.rangeUntil(this.endInclusive)
}

