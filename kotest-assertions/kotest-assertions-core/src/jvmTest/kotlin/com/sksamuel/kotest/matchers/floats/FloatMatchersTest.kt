package com.sksamuel.kotest.matchers.floats

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.floats.shouldBeExactly
import io.kotest.matchers.floats.shouldBeGreaterThan
import io.kotest.matchers.floats.shouldBeGreaterThanOrEqual
import io.kotest.matchers.floats.shouldBeLessThan
import io.kotest.matchers.floats.shouldBeLessThanOrEqual
import io.kotest.matchers.floats.shouldBeZero
import io.kotest.matchers.floats.shouldNotBeExactly
import io.kotest.matchers.floats.shouldNotBeGreaterThan
import io.kotest.matchers.floats.shouldNotBeGreaterThanOrEqual
import io.kotest.matchers.floats.shouldNotBeLessThan
import io.kotest.matchers.floats.shouldNotBeLessThanOrEqual
import io.kotest.matchers.floats.shouldNotBeZero

class FloatMatchersTest : StringSpec() {
   init {
      "shouldBeLessThan" {
         1f shouldBeLessThan 2f
         1.99999f shouldBeLessThan 2f
         Float.MIN_VALUE shouldBeLessThan Float.MAX_VALUE
         Float.NEGATIVE_INFINITY shouldBeLessThan Float.POSITIVE_INFINITY
      }
      "shouldBeLessThanOrEqual" {
         1f shouldBeLessThanOrEqual 2f
         1.5f shouldBeLessThanOrEqual 1.5f
         1f shouldBeLessThanOrEqual 1f
         2f shouldBeLessThanOrEqual 2f
         Float.MIN_VALUE shouldBeLessThanOrEqual Float.MAX_VALUE
         Float.MIN_VALUE shouldBeLessThanOrEqual Float.MIN_VALUE
         Float.MAX_VALUE shouldBeLessThanOrEqual Float.MAX_VALUE
         Float.NEGATIVE_INFINITY shouldBeLessThanOrEqual Float.POSITIVE_INFINITY
      }
      "shouldNotBeLessThan" {
         2f shouldNotBeLessThan 1f
         2f shouldNotBeLessThan 1.999f
         Float.MAX_VALUE shouldNotBeLessThan Float.MIN_VALUE
         Float.POSITIVE_INFINITY shouldNotBeLessThan Float.NEGATIVE_INFINITY
      }
      "shouldNotBeLessThanOrEqual" {
         2f shouldNotBeLessThanOrEqual 1f
         2.000001f shouldNotBeLessThanOrEqual 2f
         Float.MAX_VALUE shouldNotBeLessThanOrEqual Float.MIN_VALUE
         Float.POSITIVE_INFINITY shouldNotBeLessThanOrEqual Float.NEGATIVE_INFINITY
      }
      "shouldBeGreaterThan" {
         2f shouldBeGreaterThan 1f
         Float.MAX_VALUE shouldBeGreaterThan Float.MIN_VALUE
         Float.POSITIVE_INFINITY shouldBeGreaterThan Float.NEGATIVE_INFINITY
      }
      "shouldBeGreaterThanOrEqual" {
         2f shouldBeGreaterThanOrEqual 1f
         2f shouldBeGreaterThanOrEqual 2f
         1f shouldBeGreaterThanOrEqual 1f
         Float.MAX_VALUE shouldBeGreaterThanOrEqual Float.MIN_VALUE
         Float.MAX_VALUE shouldBeGreaterThanOrEqual Float.MAX_VALUE
         Float.MIN_VALUE shouldBeGreaterThanOrEqual Float.MIN_VALUE
         Float.POSITIVE_INFINITY shouldBeGreaterThanOrEqual Float.NEGATIVE_INFINITY
      }
      "shouldNotBeGreaterThan" {
         1f shouldNotBeGreaterThan 2f
         1.99999f shouldNotBeGreaterThan 2f
         Float.MIN_VALUE shouldNotBeGreaterThan Float.MAX_VALUE
         Float.NEGATIVE_INFINITY shouldNotBeGreaterThan Float.POSITIVE_INFINITY
      }
      "shouldNotBeGreaterThanOrEqual" {
         1f shouldNotBeGreaterThanOrEqual 2f
         1.99999f shouldNotBeGreaterThanOrEqual 2f
         Float.MIN_VALUE shouldNotBeGreaterThanOrEqual Float.MAX_VALUE
         Float.NEGATIVE_INFINITY shouldNotBeGreaterThanOrEqual Float.POSITIVE_INFINITY
      }
      "shouldBeExactly" {
         1f shouldBeExactly 1f
         -1f shouldBeExactly -1f
         0.00002f shouldBeExactly 0.00002f
         Float.MIN_VALUE shouldBeExactly Float.MIN_VALUE
         Float.MAX_VALUE shouldBeExactly Float.MAX_VALUE
         Float.POSITIVE_INFINITY shouldBeExactly Float.POSITIVE_INFINITY
         Float.NEGATIVE_INFINITY shouldBeExactly Float.NEGATIVE_INFINITY
      }
      "shouldNotBeExactly" {
         1f shouldNotBeExactly -1f
         1f shouldNotBeExactly 1.000001f
         1f shouldNotBeExactly 0.999999f
         Float.MIN_VALUE shouldNotBeExactly Float.MAX_VALUE
         Float.MIN_VALUE shouldNotBeExactly Float.NaN
         Float.MIN_VALUE shouldNotBeExactly Float.POSITIVE_INFINITY
         Float.MIN_VALUE shouldNotBeExactly Float.NEGATIVE_INFINITY
         Float.MAX_VALUE shouldNotBeExactly Float.MIN_VALUE
         Float.MAX_VALUE shouldNotBeExactly Float.NaN
         Float.MAX_VALUE shouldNotBeExactly Float.POSITIVE_INFINITY
         Float.MAX_VALUE shouldNotBeExactly Float.NEGATIVE_INFINITY
      }
      "shouldBeZero" {
         0f.shouldBeZero()
      }
      "shouldNotBeZero" {
         0.000001f.shouldNotBeZero()
         (-0.000001f).shouldNotBeZero()
         Float.MIN_VALUE.shouldNotBeZero()
         Float.MAX_VALUE.shouldNotBeZero()
         Float.NaN.shouldNotBeZero()
         Float.POSITIVE_INFINITY.shouldNotBeZero()
         Float.NEGATIVE_INFINITY.shouldNotBeZero()
      }
   }

}
