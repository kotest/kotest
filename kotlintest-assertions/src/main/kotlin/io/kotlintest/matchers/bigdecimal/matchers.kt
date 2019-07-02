package io.kotlintest.matchers.bigdecimal

import io.kotlintest.shouldBe
import java.math.BigDecimal

fun BigDecimal.shouldBeZero() = this shouldBe BigDecimal.ZERO
infix fun BigDecimal.shouldHavePrecision(precision: Int) = this.precision() shouldBe precision
infix fun BigDecimal.shouldHaveScale(scale: Int) = this.scale() shouldBe scale
