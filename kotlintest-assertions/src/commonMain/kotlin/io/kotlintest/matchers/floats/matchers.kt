package io.kotlintest.matchers.floats

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import kotlin.math.abs

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


infix fun Float.plusOrMinus(tolerance: Float): FloatToleranceMatcher = FloatToleranceMatcher(this, tolerance)

fun exactly(d: Float): Matcher<Float> = object : Matcher<Float> {
  override fun test(value: Float) = Result(value == d,
          "$value is not equal to expected value $d",
          "$value should not be equal to $d")
}

class FloatToleranceMatcher(val expected: Float, val tolerance: Float) : Matcher<Float> {
  
  override fun test(value: Float): Result {
    return if (Float.NaN == expected && Float.NaN == value) {
      println("[WARN] By design, Float.Nan != Float.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776")
      Result(
              false,
              "By design, Float.Nan != Float.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776",
              "By design, Float.Nan != Float.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776"
      )
    } else {
      if (tolerance == 0.0F)
        println("[WARN] When comparing Float consider using tolerance, eg: a shouldBe b plusOrMinus c")
      val diff = abs(value - expected)
      Result(diff <= tolerance,
              "$value should be equal to $expected",
              "$value should not be equal to $expected")
    }
  }
  
  infix fun plusOrMinus(tolerance: Float): FloatToleranceMatcher = FloatToleranceMatcher(expected, tolerance)
}
