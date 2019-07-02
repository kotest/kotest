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
    }
}
