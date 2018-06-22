package io.kotlintest.matchers.doubles

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.matchers.between
import io.kotlintest.matchers.exactly
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe

fun Double.shouldBeExactly(other: Double) = this shouldBe exactly(other)
fun Double.shouldNotBeExactly(other: Double) = this shouldBe exactly(other)
fun Double.shouldBeBetween(a: Double, b: Double, tolerance: Double) = this shouldNotBe between(a, b, tolerance)
fun Double.shouldNotBeBetween(a: Double, b: Double, tolerance: Double) = this shouldNotBe between(a, b, tolerance)

fun Double.shouldBeLessThan(x: Double) = this shouldBe lt(x)
fun Double.shouldNotBeLessThan(x: Double) = this shouldNotBe lt(x)
fun lt(x: Double) = beLessThan(x)
fun beLessThan(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = Result(value < x, "$value should be < $x", "$value should not be < $x")
}

fun Double.shouldBePositive() = this shouldBe positive()
fun positive() = object : Matcher<Double> {
  override fun test(value: Double) = Result(value > 0.0, "$value should be > 0.0", "$value should not be > 0.0")
}

fun Double.shouldBeNegative() = this shouldBe negative()
fun negative() = object : Matcher<Double> {
  override fun test(value: Double) = Result(value < 0.0, "$value should be < 0.0", "$value should not be < 0.0")
}


fun Double.shouldBeLessThanOrEqual(x: Double) = this shouldBe lte(x)
fun Double.shouldNotBeLessThanOrEqual(x: Double) = this shouldNotBe lte(x)
fun lte(x: Double) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = Result(value <= x, "$value should be <= $x", "$value should not be <= $x")
}

fun Double.shouldBeGreaterThan(x: Double) = this shouldBe gt(x)
fun Double.shouldNotBeGreaterThan(x: Double) = this shouldNotBe gt(x)
fun gt(x: Double) = beGreaterThan(x)
fun beGreaterThan(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = Result(value > x, "$value should be > $x", "$value should not be > $x")
}

fun Double.shouldBeGreaterThanOrEqual(x: Double) = this shouldBe gte(x)
fun Double.shouldNotBeGreaterThanOrEqual(x: Double) = this shouldNotBe gte(x)
fun gte(x: Double) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = Result(value >= x, "$value should be >= $x", "$value should not be >= $x")
}