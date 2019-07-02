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
    }
}
