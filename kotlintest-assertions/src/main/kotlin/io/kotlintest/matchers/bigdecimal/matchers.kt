package io.kotlintest.matchers.bigdecimal

import io.kotlintest.matchers.gte
import io.kotlintest.matchers.lt
import io.kotlintest.shouldBe
import java.math.BigDecimal

fun BigDecimal.shouldBeZero() = this shouldBe BigDecimal.ZERO
infix fun BigDecimal.shouldHavePrecision(precision: Int) = this.precision() shouldBe precision
infix fun BigDecimal.shouldHaveScale(scale: Int) = this.scale() shouldBe scale
fun BigDecimal.shouldBePositive() = this shouldBe gte(BigDecimal.ZERO)
fun BigDecimal.shouldBeNegative() = this shouldBe lt(BigDecimal.ZERO)
