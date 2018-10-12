package io.kotlintest.matchers.doubles

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import kotlin.math.abs

infix fun Double.shouldBeExactly(other: Double) = this shouldBe exactly(other)
infix fun Double.shouldNotBeExactly(other: Double) = this shouldNotBe exactly(other)
fun exactly(d: Double): Matcher<Double> = object : Matcher<Double> {
  override fun test(value: Double) = Result(value == d, "$value is not equal to expected value $d", "$value should not equal $d")
}

fun Double.shouldBeBetween(a: Double, b: Double, tolerance: Double) = this shouldBe between(a, b, tolerance)
fun Double.shouldNotBeBetween(a: Double, b: Double, tolerance: Double) = this shouldNotBe between(a, b, tolerance)
fun between(a: Double, b: Double, tolerance: Double): Matcher<Double> = object : Matcher<Double> {
  override fun test(value: Double): Result {
    val differenceToMinimum = value - a
    val differenceToMaximum = b - value
    
    if (differenceToMinimum < 0 && abs(differenceToMinimum) > tolerance) {
      return Result(false, "$value should be bigger than $a", "$value should not be bigger than $a")
    }
    
    if (differenceToMaximum < 0 && abs(differenceToMaximum) > tolerance) {
      return Result(false, "$value should be smaller than $b", "$value should not be smaller than $b")
    }
    
    return Result(true, "$value should be smaller than $b and bigger than $a", "$value should not be smaller than $b and should not be bigger than $a")
  }
}

infix fun Double.shouldBeLessThan(x: Double) = this shouldBe lt(x)
infix fun Double.shouldNotBeLessThan(x: Double) = this shouldNotBe lt(x)
fun lt(x: Double) = beLessThan(x)
fun beLessThan(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = Result(value < x, "$value should be < $x", "$value should not be < $x")
}

fun Double.shouldBePositive() = this shouldBe positive()
fun Double.shouldNotBePositive() = this shouldNotBe positive()
fun positive() = object : Matcher<Double> {
  override fun test(value: Double) = Result(value > 0.0, "$value should be > 0.0", "$value should not be > 0.0")
}

fun Double.shouldBeNegative() = this shouldBe negative()
fun Double.shouldNotBeNegative() = this shouldNotBe negative()
fun negative() = object : Matcher<Double> {
  override fun test(value: Double) = Result(value < 0.0, "$value should be < 0.0", "$value should not be < 0.0")
}


infix fun Double.shouldBeLessThanOrEqual(x: Double) = this shouldBe lte(x)
infix fun Double.shouldNotBeLessThanOrEqual(x: Double) = this shouldNotBe lte(x)
fun lte(x: Double) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = Result(value <= x, "$value should be <= $x", "$value should not be <= $x")
}

infix fun Double.shouldBeGreaterThan(x: Double) = this shouldBe gt(x)
infix fun Double.shouldNotBeGreaterThan(x: Double) = this shouldNotBe gt(x)
fun gt(x: Double) = beGreaterThan(x)
fun beGreaterThan(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = Result(value > x, "$value should be > $x", "$value should not be > $x")
}

infix fun Double.shouldBeGreaterThanOrEqual(x: Double) = this shouldBe gte(x)
infix fun Double.shouldNotBeGreaterThanOrEqual(x: Double) = this shouldNotBe gte(x)
fun gte(x: Double) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = Result(value >= x, "$value should be >= $x", "$value should not be >= $x")
}


infix fun Double.plusOrMinus(tolerance: Double): ToleranceMatcher = ToleranceMatcher(this, tolerance)


class ToleranceMatcher(private val expected: Double?, private val tolerance: Double) : Matcher<Double?> {
  override fun test(value: Double?): Result {
    return if(value == null || expected == null) {
      Result(value == expected, "$value should be equal to $expected", "$value should not be equal to $expected")
    } else if (Double.NaN == expected && Double.NaN == value) {
      println("[WARN] By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776")
      Result(false,
             "By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776",
             "By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776"
      )
    } else {
      if (tolerance == 0.0)
        println("[WARN] When comparing doubles consider using tolerance, eg: a shouldBe (b plusOrMinus c)")
      val diff = Math.abs(value - expected)
      Result(diff <= tolerance, "$value should be equal to $expected", "$value should not be equal to $expected")
    }
  }
}