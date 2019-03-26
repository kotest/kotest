package io.kotlintest.matchers.floats

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe

fun lt(x: Float) = beLessThan(x)
fun beLessThan(x: Float) = object : Matcher<Float> {
  override fun test(value: Float) = Result(value < x,
      "$value should be < $x",
      "$value should not be < $x")
}

fun lte(x: Float) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Float) = object : Matcher<Float> {
  override fun test(value: Float) = Result(value <= x,
      "$value should be <= $x",
      "$value should not be <= $x")
}

fun gt(x: Float) = beGreaterThan(x)
fun beGreaterThan(x: Float) = object : Matcher<Float> {
  override fun test(value: Float) = Result(value > x,
      "$value should be > $x",
      "$value should not be > $x")
}

fun gte(x: Float) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Float) = object : Matcher<Float> {
  override fun test(value: Float) = Result(value >= x,
      "$value should be >= $x",
      "$value should not be >= $x")
}

infix fun Float.shouldBeLessThan(x: Float) = this shouldBe lt(x)
infix fun Float.shouldNotBeLessThan(x: Float) = this shouldNotBe lt(x)

infix fun Float.shouldBeLessThanOrEqual(x: Float) = this shouldBe lte(x)
infix fun Float.shouldNotBeLessThanOrEqual(x: Float) = this shouldNotBe lte(x)

infix fun Float.shouldBeGreaterThan(x: Float) = this shouldBe gt(x)
infix fun Float.shouldNotBeGreaterThan(x: Float) = this shouldNotBe gt(x)

infix fun Float.shouldBeGreaterThanOrEqual(x: Float) = this shouldBe gte(x)
infix fun Float.shouldNotBeGreaterThanOrEqual(x: Float) = this shouldNotBe gte(x)
