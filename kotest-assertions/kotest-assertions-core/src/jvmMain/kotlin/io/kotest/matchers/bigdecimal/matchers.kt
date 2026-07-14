package io.kotest.matchers.bigdecimal

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.comparables.gt
import io.kotest.matchers.comparables.gte
import io.kotest.matchers.comparables.lt
import io.kotest.matchers.comparables.lte
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal

@IgnorableReturnValue
fun BigDecimal.shouldBeZero() = this shouldBe BigDecimal.ZERO
@IgnorableReturnValue
fun BigDecimal.shouldBePositive() = this shouldBe gt(BigDecimal.ZERO)
@IgnorableReturnValue
fun BigDecimal.shouldBeNegative() = this shouldBe lt(BigDecimal.ZERO)
@IgnorableReturnValue
fun BigDecimal.shouldNotBePositive() = this shouldNotBe gt(BigDecimal.ZERO)
@IgnorableReturnValue
fun BigDecimal.shouldNotBeNegative() = this shouldNotBe lt(BigDecimal.ZERO)

@IgnorableReturnValue
infix fun BigDecimal.shouldHavePrecision(precision: Int) = this.precision() shouldBe precision

@IgnorableReturnValue
infix fun BigDecimal.shouldHaveScale(scale: Int) = this.scale() shouldBe scale
@IgnorableReturnValue
infix fun BigDecimal.shouldNotHaveScale(scale: Int) = this.scale() shouldNotBe scale

@IgnorableReturnValue
infix fun BigDecimal.shouldBeLessThan(other: BigDecimal) = this shouldBe lt(other)
@Deprecated(
   "use Kotest's shouldBeLessThanOrEqual",
   ReplaceWith("shouldBeLessThanOrEqual", "io.kotest.matchers.bigdecimal")
)
@IgnorableReturnValue
infix fun BigDecimal.shouldBeLessThanOrEquals(other: BigDecimal) = this.shouldBeLessThanOrEqual(other)
@IgnorableReturnValue
infix fun BigDecimal.shouldBeAtMost(other: BigDecimal) = this.shouldBeLessThanOrEqual(other)
@IgnorableReturnValue
infix fun BigDecimal.shouldBeLessThanOrEqual(other: BigDecimal) = this shouldBe lte(other)
@IgnorableReturnValue
infix fun BigDecimal.shouldNotBeLessThan(other: BigDecimal) = this shouldNotBe lt(other)
@Deprecated(
   "use Kotest's shouldNotBeLessThanOrEqual",
   ReplaceWith("shouldNotBeLessThanOrEqual", "io.kotest.matchers.bigdecimal")
)
@IgnorableReturnValue
infix fun BigDecimal.shouldNotBeLessThanOrEquals(other: BigDecimal) = this.shouldNotBeLessThanOrEqual(other)
@IgnorableReturnValue
infix fun BigDecimal.shouldNotBeAtMost(other: BigDecimal) = this.shouldNotBeLessThanOrEqual(other)
@IgnorableReturnValue
infix fun BigDecimal.shouldNotBeLessThanOrEqual(other: BigDecimal) = this shouldNotBe lte(other)

@IgnorableReturnValue
infix fun BigDecimal.shouldBeGreaterThan(other: BigDecimal) = this shouldBe gt(other)
@Deprecated(
   "use Kotest's shouldBeGreaterThanOrEqual",
   ReplaceWith("shouldBeGreaterThanOrEqual", "io.kotest.matchers.bigdecimal")
)
@IgnorableReturnValue
infix fun BigDecimal.shouldBeGreaterThanOrEquals(other: BigDecimal) = this.shouldBeGreaterThanOrEqual(other)
@IgnorableReturnValue
infix fun BigDecimal.shouldBeAtLeast(other: BigDecimal) = this.shouldBeGreaterThanOrEqual(other)
@IgnorableReturnValue
infix fun BigDecimal.shouldBeGreaterThanOrEqual(other: BigDecimal) = this shouldBe gte(other)
@IgnorableReturnValue
infix fun BigDecimal.shouldNotBeGreaterThan(other: BigDecimal) = this shouldNotBe gt(other)
@Deprecated(
   "use Kotest's shouldNotBeGreaterThanOrEqual",
   ReplaceWith("shouldNotBeGreaterThanOrEqual", "io.kotest.matchers.bigdecimal")
)
@IgnorableReturnValue
infix fun BigDecimal.shouldNotBeGreaterThanOrEquals(other: BigDecimal) = this.shouldNotBeGreaterThanOrEqual(other)
@IgnorableReturnValue
infix fun BigDecimal.shouldNotBeAtLeast(other: BigDecimal) = this.shouldNotBeGreaterThanOrEqual(other)
@IgnorableReturnValue
infix fun BigDecimal.shouldNotBeGreaterThanOrEqual(other: BigDecimal) = this shouldNotBe gte(other)

@Deprecated("use <T: Comparable<T>> shouldBeIn",
   ReplaceWith("this shouldBeIn (range)", "io.kotest.matchers.ranges.shouldBeIn")
)
@IgnorableReturnValue
infix fun BigDecimal.shouldBeInRange(range: ClosedRange<BigDecimal>) = this should beInClosedRange(range)
@Deprecated("use <T: Comparable<T>> shouldNotBeIn",
   ReplaceWith("this shouldNotBeIn (range)", "io.kotest.matchers.ranges.shouldNotBeIn")
)
@IgnorableReturnValue
infix fun BigDecimal.shouldNotBeInRange(range: ClosedRange<BigDecimal>) = this shouldNot beInClosedRange(range)
fun beInClosedRange(range: ClosedRange<BigDecimal>) = object : Matcher<BigDecimal> {
   override fun test(value: BigDecimal) = MatcherResult(
      range.contains(value),
      { "Value $value should be in range from ${range.start} to ${range.endInclusive} (Inclusive)" },
      { "Value $value should not be in range from ${range.start} to ${range.endInclusive} (Inclusive)" }
   )
}

@IgnorableReturnValue
infix fun BigDecimal.shouldBeEqualIgnoringScale(other: BigDecimal) = this should beEqualIgnoringScale(other)
@IgnorableReturnValue
infix fun BigDecimal.shouldNotBeEqualIgnoringScale(other: BigDecimal) = this shouldNot beEqualIgnoringScale(other)

fun beEqualIgnoringScale(other: BigDecimal) = object : Matcher<BigDecimal> {
   override fun test(value: BigDecimal) = MatcherResult(
      value.compareTo(other) == 0,
      { "BigDecimal $value should be equal ignoring scale to $other" },
      { "BigDecimal $value should not be equal ignoring scale to $other" },
   )
}

