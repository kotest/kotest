package com.sksamuel.kotest.matchers.floats

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.floats.shouldBeNegative
import io.kotest.matchers.floats.shouldBePositive
import io.kotest.matchers.floats.shouldNotBeNegative
import io.kotest.matchers.floats.shouldNotBePositive

class PositiveNegativeTest : FreeSpec() {
   init {

      "a float should be positive" - {
         "if larger than zero" {
            0.1F.shouldBePositive()
            Float.POSITIVE_INFINITY.shouldBePositive()
            0.0F.shouldNotBePositive()
            shouldFail {
               0.0F.shouldBePositive()
            }
            shouldFail {
               (-0.1F).shouldBePositive()
            }
         }

         "otherwise false" {
            (-0.1F).shouldNotBePositive()
            Float.NEGATIVE_INFINITY.shouldNotBePositive()
            shouldFail {
               (0.1F).shouldNotBePositive()
            }
         }
      }

      "a float should be negative" - {
         "if less than zero" {
            (-0.1F).shouldBeNegative()
            Float.NEGATIVE_INFINITY.shouldBeNegative()
            shouldFail {
               0.0F.shouldBeNegative()
            }
            shouldFail {
               0.1F.shouldBeNegative()
            }
         }

         "otherwise false" {
            0.1F.shouldNotBeNegative()
            Float.POSITIVE_INFINITY.shouldNotBeNegative()
            shouldFail {
               (-0.1F).shouldNotBeNegative()
            }
         }
      }
   }
}
