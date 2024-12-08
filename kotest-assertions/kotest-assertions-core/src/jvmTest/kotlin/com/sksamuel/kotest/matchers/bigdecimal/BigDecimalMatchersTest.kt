package com.sksamuel.kotest.matchers.bigdecimal

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.bigdecimal.shouldBeGreaterThan
import io.kotest.matchers.bigdecimal.shouldBeGreaterThanOrEquals
import io.kotest.matchers.bigdecimal.shouldBeInRange
import io.kotest.matchers.bigdecimal.shouldBeLessThan
import io.kotest.matchers.bigdecimal.shouldBeLessThanOrEquals
import io.kotest.matchers.bigdecimal.shouldBeNegative
import io.kotest.matchers.bigdecimal.shouldBePositive
import io.kotest.matchers.bigdecimal.shouldBeZero
import io.kotest.matchers.bigdecimal.shouldHavePrecision
import io.kotest.matchers.bigdecimal.shouldNotBeGreaterThan
import io.kotest.matchers.bigdecimal.shouldNotBeGreaterThanOrEquals
import io.kotest.matchers.bigdecimal.shouldNotBeInRange
import io.kotest.matchers.bigdecimal.shouldNotBeLessThan
import io.kotest.matchers.bigdecimal.shouldNotBeLessThanOrEquals
import io.kotest.matchers.bigdecimal.shouldNotBeNegative
import io.kotest.matchers.bigdecimal.shouldNotBePositive
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
    "shouldBeGreaterThanOrEquals" {
      BigDecimal.ONE shouldBeGreaterThanOrEquals BigDecimal.ZERO
      BigDecimal.ONE shouldBeGreaterThanOrEquals BigDecimal.ONE
      BigDecimal.TEN shouldBeGreaterThanOrEquals BigDecimal.ONE
      BigDecimal.TEN shouldBeGreaterThanOrEquals BigDecimal.TEN
      BigDecimal.ONE shouldNotBeGreaterThanOrEquals BigDecimal.TEN
      BigDecimal.ZERO shouldNotBeGreaterThanOrEquals BigDecimal.ONE
    }
    "shouldBeLessThan" {
      BigDecimal.ZERO shouldBeLessThan BigDecimal.ONE
      BigDecimal.ONE shouldBeLessThan BigDecimal.TEN
      BigDecimal.ONE shouldNotBeLessThan BigDecimal.ONE
      BigDecimal.TEN shouldNotBeLessThan BigDecimal.ONE
      BigDecimal.TEN shouldNotBeLessThan BigDecimal.TEN
    }
    "shouldBeLessThanOrEquals" {
      BigDecimal.ZERO shouldBeLessThanOrEquals BigDecimal.ONE
      BigDecimal.ONE shouldBeLessThanOrEquals BigDecimal.ONE
      BigDecimal.ONE shouldBeLessThanOrEquals BigDecimal.TEN
      BigDecimal.TEN shouldBeLessThanOrEquals BigDecimal.TEN
      BigDecimal.TEN shouldNotBeLessThanOrEquals BigDecimal.ONE
      BigDecimal.ONE shouldNotBeLessThanOrEquals BigDecimal.ZERO
    }
    "shouldBeInRange" {
      BigDecimal.ZERO shouldBeInRange BigDecimal(-1) .. BigDecimal(1)
      BigDecimal.ONE shouldBeInRange BigDecimal(-1) .. BigDecimal(1)
      (-BigDecimal.ONE) shouldBeInRange BigDecimal(-1) .. BigDecimal(1)
      BigDecimal.TEN shouldNotBeInRange BigDecimal(-1) .. BigDecimal(1)
    }
  }
}
