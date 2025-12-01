package com.sksamuel.kotest.matchers.bigdecimal

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.bigdecimal.shouldBeAtLeast
import io.kotest.matchers.bigdecimal.shouldBeAtMost
import io.kotest.matchers.bigdecimal.shouldBeGreaterThan
import io.kotest.matchers.bigdecimal.shouldBeGreaterThanOrEqual
import io.kotest.matchers.bigdecimal.shouldBeLessThan
import io.kotest.matchers.bigdecimal.shouldBeLessThanOrEqual
import io.kotest.matchers.bigdecimal.shouldBeNegative
import io.kotest.matchers.bigdecimal.shouldBePositive
import io.kotest.matchers.bigdecimal.shouldBeZero
import io.kotest.matchers.bigdecimal.shouldHavePrecision
import io.kotest.matchers.bigdecimal.shouldNotBeAtLeast
import io.kotest.matchers.bigdecimal.shouldNotBeAtMost
import io.kotest.matchers.bigdecimal.shouldNotBeGreaterThan
import io.kotest.matchers.bigdecimal.shouldNotBeGreaterThanOrEqual
import io.kotest.matchers.bigdecimal.shouldNotBeLessThan
import io.kotest.matchers.bigdecimal.shouldNotBeLessThanOrEqual
import io.kotest.matchers.bigdecimal.shouldNotBeNegative
import io.kotest.matchers.bigdecimal.shouldNotBePositive
import io.kotest.matchers.ranges.shouldBeIn
import io.kotest.matchers.ranges.shouldNotBeIn
import java.math.BigDecimal

class BigDecimalMatchersTest : StringSpec() {
   init {
      "shouldBeZero" {
         BigDecimal.ZERO.shouldBeZero()
         BigDecimal(0).shouldBeZero()
         0.toBigDecimal().shouldBeZero()
      }
      "shouldHavePrecision" {
         BigDecimal(10).setScale(3) shouldHavePrecision 5
         BigDecimal(10.1) shouldHavePrecision 51
         10.1.toBigDecimal() shouldHavePrecision 3
         BigDecimal.ZERO shouldHavePrecision 1
      }
      "shouldBePositive" {
         BigDecimal(10).shouldBePositive()
         BigDecimal.ONE.shouldBePositive()
         BigDecimal(0.1).shouldBePositive()
         0.1.toBigDecimal().shouldBePositive()

         shouldThrowAny { BigDecimal(-1).shouldBePositive() }
         shouldThrowAny { BigDecimal.ZERO.shouldBePositive() }
      }
      "shouldBeNegative" {
         BigDecimal(-1).shouldBeNegative()
         (-1).toBigDecimal().shouldBeNegative()
         BigDecimal(-0.1).shouldBeNegative()
         BigDecimal(1).minus(BigDecimal(2)).shouldBeNegative()

         shouldThrowAny { BigDecimal(1).shouldBeNegative() }
         shouldThrowAny { BigDecimal.ZERO.shouldBeNegative() }
      }
      "shouldNotBePositive" {
         BigDecimal(-1).shouldNotBePositive()
         BigDecimal.ZERO.shouldNotBePositive()

         shouldThrowAny { BigDecimal(1).shouldNotBePositive() }
      }
      "shouldNotBeNegative" {
         BigDecimal(1).shouldNotBeNegative()
         BigDecimal.ZERO.shouldNotBeNegative()

         shouldThrowAny { BigDecimal(-1).shouldNotBeNegative() }
      }
      "shouldBeGreaterThan" {
         BigDecimal.ONE shouldBeGreaterThan BigDecimal.ZERO
         BigDecimal.TEN shouldBeGreaterThan BigDecimal.ONE
         BigDecimal.ONE shouldNotBeGreaterThan BigDecimal.ONE
         BigDecimal.ONE shouldNotBeGreaterThan BigDecimal.TEN
         BigDecimal.TEN shouldNotBeGreaterThan BigDecimal.TEN
      }
      "shouldBeGreaterThanOrEqual" {
         BigDecimal.ONE shouldBeGreaterThanOrEqual BigDecimal.ZERO
         BigDecimal.ONE shouldBeGreaterThanOrEqual BigDecimal.ONE
         BigDecimal.TEN shouldBeGreaterThanOrEqual BigDecimal.ONE
         BigDecimal.TEN shouldBeGreaterThanOrEqual BigDecimal.TEN
         BigDecimal.ONE shouldNotBeGreaterThanOrEqual BigDecimal.TEN
         BigDecimal.ZERO shouldNotBeGreaterThanOrEqual BigDecimal.ONE
      }
      "shouldBeAtLeast" {
         BigDecimal.ONE shouldBeAtLeast BigDecimal.ZERO
         BigDecimal.ONE shouldBeAtLeast BigDecimal.ONE
         BigDecimal.TEN shouldBeAtLeast BigDecimal.ONE
         BigDecimal.TEN shouldBeAtLeast BigDecimal.TEN
         BigDecimal.ONE shouldNotBeAtLeast BigDecimal.TEN
         BigDecimal.ZERO shouldNotBeAtLeast BigDecimal.ONE
      }
      "shouldBeLessThan" {
         BigDecimal.ZERO shouldBeLessThan BigDecimal.ONE
         BigDecimal.ONE shouldBeLessThan BigDecimal.TEN
         BigDecimal.ONE shouldNotBeLessThan BigDecimal.ONE
         BigDecimal.TEN shouldNotBeLessThan BigDecimal.ONE
         BigDecimal.TEN shouldNotBeLessThan BigDecimal.TEN
      }
      "shouldBeLessThanOrEqual" {
         BigDecimal.ZERO shouldBeLessThanOrEqual BigDecimal.ONE
         BigDecimal.ONE shouldBeLessThanOrEqual BigDecimal.ONE
         BigDecimal.ONE shouldBeLessThanOrEqual BigDecimal.TEN
         BigDecimal.TEN shouldBeLessThanOrEqual BigDecimal.TEN
         BigDecimal.TEN shouldNotBeLessThanOrEqual BigDecimal.ONE
         BigDecimal.ONE shouldNotBeLessThanOrEqual BigDecimal.ZERO
      }
      "shouldBeAtMost" {
         BigDecimal.ZERO shouldBeAtMost BigDecimal.ONE
         BigDecimal.ONE shouldBeAtMost BigDecimal.ONE
         BigDecimal.ONE shouldBeAtMost BigDecimal.TEN
         BigDecimal.TEN shouldBeAtMost BigDecimal.TEN
         BigDecimal.TEN shouldNotBeAtMost BigDecimal.ONE
         BigDecimal.ONE shouldNotBeAtMost BigDecimal.ZERO
      }
      "shouldBeInRange" {
         BigDecimal.ZERO shouldBeIn BigDecimal(-1)..BigDecimal(1)
         BigDecimal.ONE shouldBeIn BigDecimal(-1)..BigDecimal(1)
         (-BigDecimal.ONE) shouldBeIn BigDecimal(-1)..BigDecimal(1)
         BigDecimal.TEN shouldNotBeIn (BigDecimal(-1)..BigDecimal(1))
      }
   }
}
