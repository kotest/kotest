package com.sksamuel.kotest.matchers.floats

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.floats.shouldBeNegativeInfinity
import io.kotest.matchers.floats.shouldBePositiveInfinity
import io.kotest.matchers.floats.shouldNotBeNegativeInfinity
import io.kotest.matchers.floats.shouldNotBePositiveInfinity

class InfinityTest : FreeSpec() {
   init {

      "a float should be positive infinity" - {
         "if Float.POSITIVE_INFINITY" {
            Float.POSITIVE_INFINITY.shouldBePositiveInfinity()
            shouldFail {
               1.0F.shouldBePositiveInfinity()
            }
         }

         "otherwise false" {
            1.0F.shouldNotBePositiveInfinity()
            Float.NEGATIVE_INFINITY.shouldNotBePositiveInfinity()
            shouldFail {
               Float.POSITIVE_INFINITY.shouldNotBePositiveInfinity()
            }
         }
      }

      "a float should be negative infinity" - {
         "if Float.NEGATIVE_INFINITY" {
            Float.NEGATIVE_INFINITY.shouldBeNegativeInfinity()
            shouldFail {
               1.0F.shouldBeNegativeInfinity()
            }
         }

         "otherwise false" {
            1.0F.shouldNotBeNegativeInfinity()
            Float.POSITIVE_INFINITY.shouldNotBeNegativeInfinity()
            shouldFail {
               Float.NEGATIVE_INFINITY.shouldNotBeNegativeInfinity()
            }
         }
      }
   }
}
