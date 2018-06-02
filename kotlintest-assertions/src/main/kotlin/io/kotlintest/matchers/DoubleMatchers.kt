package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result

infix fun Double.plusOrMinus(tolerance: Double): ToleranceMatcher = ToleranceMatcher(this, tolerance)

fun exactly(d: Double): Matcher<Double> = object : Matcher<Double> {
  override fun test(value: Double) = Result(value == d, "$value is not equal to expected value $d", "$value should not equal $d")
}

fun between(a: Double, b: Double, tolerance: Double): Matcher<Double> = object : Matcher<Double> {
  override fun test(value: Double): Result {
    val differenceToMinimum = value - a
    val differenceToMaximum = b - value

    if (differenceToMinimum < 0 && differenceToMinimum > tolerance) {
      return Result(false, "$value should be bigger than $a", "$value should not be bigger than $a")
    }

    if (differenceToMaximum < 0 && differenceToMaximum > tolerance) {
      return Result(false, "$value should be smaller than $b", "$value should not be smaller than $b")
    }

    return Result(true, "$value should be smaller than $b and bigger than $a", "$value should not be smaller and $b and bigger than $a")
  }
}

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
