package io.kotest.matchers.bigdecimal

import io.kotest.matchers.comparables.gt
import io.kotest.matchers.comparables.gte
import io.kotest.matchers.comparables.lt
import io.kotest.matchers.comparables.lte
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal

fun BigDecimal.shouldBeZero() = this shouldBe BigDecimal.ZERO
fun BigDecimal.shouldBePositive() = this shouldBe gt(BigDecimal.ZERO)
fun BigDecimal.shouldBeNegative() = this shouldBe lt(BigDecimal.ZERO)

infix fun BigDecimal.shouldHavePrecision(precision: Int) = this.precision() shouldBe precision

infix fun BigDecimal.shouldHaveScale(scale: Int) = this.scale() shouldBe scale
infix fun BigDecimal.shouldNotHaveScale(scale: Int) = this.scale() shouldNotBe scale

infix fun BigDecimal.shouldBeLessThan(other: BigDecimal) = this shouldBe lt(other)
infix fun BigDecimal.shouldBeLessThanOrEquals(other: BigDecimal) = this shouldBe lte(other)
infix fun BigDecimal.shouldNotBeLessThan(other: BigDecimal) = this shouldNotBe lt(other)
infix fun BigDecimal.shouldNotBeLessThanOrEquals(other: BigDecimal) = this shouldNotBe lte(other)

infix fun BigDecimal.shouldBeGreaterThan(other: BigDecimal) = this shouldBe gt(other)
infix fun BigDecimal.shouldBeGreaterThanOrEquals(other: BigDecimal) = this shouldBe gte(other)
infix fun BigDecimal.shouldNotBeGreaterThan(other: BigDecimal) = this shouldNotBe gt(other)
infix fun BigDecimal.shouldNotBeGreaterThanOrEquals(other: BigDecimal) = this shouldNotBe gte(other)

infix fun BigDecimal.shouldBeInRange(range: ClosedRange<BigDecimal>) = this should beInClosedRange(range)
infix fun BigDecimal.shouldNotBeInRange(range: ClosedRange<BigDecimal>) = this shouldNot beInClosedRange(range)
fun beInClosedRange(range: ClosedRange<BigDecimal>) = object : Matcher<BigDecimal> {
   override fun test(value: BigDecimal) = MatcherResult(
      range.contains(value),
      { "Value $value should be in range from ${range.start} to ${range.endInclusive} (Inclusive)" },
      {
         "Value $value should not be in range from ${range.start} to ${range.endInclusive} (Inclusive)"
      })
}
