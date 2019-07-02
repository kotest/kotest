package com.sksamuel.kotlintest.matchers.bigdecimal

import io.kotlintest.matchers.bigdecimal.*
import io.kotlintest.specs.StringSpec
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
        "shouldHaveScale" {
            BigDecimal(10).setScale(3) shouldHaveScale  3
            BigDecimal(10.1) shouldHaveScale 49
            10.444.toBigDecimal() shouldHaveScale 3
            0.toBigDecimal() shouldHaveScale 0
            BigDecimal.ZERO shouldHaveScale 0
        }
        "the big decimal should be positive" {
            BigDecimal(10).shouldBePositive()
            BigDecimal.ZERO.shouldBePositive()
            BigDecimal(0.1).shouldBePositive()
            0.1.toBigDecimal().shouldBePositive()
        }
    }
}
