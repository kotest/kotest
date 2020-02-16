package io.kotest.matchers.longs

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

fun Long.shouldBePositive() = this shouldBe positiveL()
fun positiveL() = object : Matcher<Long> {
  override fun test(value: Long) = MatcherResult(value > 0, "$value should be > 0", "$value should not be > 0")
}

fun Long.shouldBeNegative() = this shouldBe negativeL()
fun negativeL() = object : Matcher<Long> {
  override fun test(value: Long) = MatcherResult(value < 0, "$value should be < 0", "$value should not be < 0")
}

fun Long.shouldBeEven() = this should lbeEven()
fun Long.shouldNotBeEven() = this shouldNot lbeEven()
fun lbeEven() = object : Matcher<Long> {
  override fun test(value: Long): MatcherResult =
      MatcherResult(value % 2 == 0L, "$value should be even", "$value should be odd")
}

fun Long.shouldBeOdd() = this should lbeOdd()
fun Long.shouldNotBeOdd() = this shouldNot lbeOdd()
fun lbeOdd() = object : Matcher<Long> {
  override fun test(value: Long): MatcherResult =
      MatcherResult(value % 2 == 1L, "$value should be odd", "$value should be even")
}

infix fun Long.shouldBeLessThan(x: Long) = this shouldBe lt(x)
infix fun Long.shouldNotBeLessThan(x: Long) = this shouldNotBe lt(x)

infix fun Long.shouldBeLessThanOrEqual(x: Long) = this shouldBe lte(x)
infix fun Long.shouldNotBeLessThanOrEqual(x: Long) = this shouldNotBe lte(x)

infix fun Long.shouldBeGreaterThan(x: Long) = this shouldBe gt(x)
infix fun Long.shouldNotBeGreaterThan(x: Long) = this shouldNotBe gt(x)

infix fun Long.shouldBeGreaterThanOrEqual(x: Long) = this shouldBe gte(x)
infix fun Long.shouldNotBeGreaterThanOrEqual(x: Long) = this shouldNotBe gte(x)

infix fun Long.shouldBeExactly(x: Long) = this shouldBe exactly(x)
infix fun Long.shouldNotBeExactly(x: Long) = this shouldNotBe exactly(x)

fun Long.shouldBeZero() = this shouldBeExactly 0L
fun Long.shouldNotBeZero() = this shouldNotBeExactly 0L
