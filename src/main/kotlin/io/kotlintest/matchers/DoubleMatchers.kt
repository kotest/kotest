package io.kotlintest.matchers

infix fun Double.plusOrMinus(tolerance: Double): ToleranceMatcher = ToleranceMatcher(this, tolerance)

fun exactly(d: Double): Matcher<Double> = object : Matcher<Double> {
  override fun test(value: Double) = Result(value == d, "$value is not equal to expected value $d")
}

fun between(a: Double, b: Double, tolerance: Double ): Matcher<Double> = object : Matcher<Double> {
    override fun test(value: Double): Result {
        val differenceToMinimum = value - a
        val differenceToMaximum = b - value

        if (differenceToMinimum < 0 && differenceToMinimum > tolerance) {
            return Result(false, "$value should be bigger than $a")
        }
        if (differenceToMaximum < 0 && differenceToMaximum > tolerance) {
            return Result(false, "$value should be smaller than $b")
        }
        return Result(true, "")
    }
}

class ToleranceMatcher(val expected: Double, val tolerance: Double) : Matcher<Double> {

  override fun test(value: Double): Result {
    return if (Double.NaN.equals(expected) && Double.NaN.equals(value)) {
      println("[WARN] By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776")
      Result(false, "By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776")
    } else {
      if (tolerance == 0.0)
        println("[WARN] When comparing doubles consider using tolerance, eg: a shouldBe b plusOrMinus c")
      val diff = Math.abs(value - expected)
      Result(diff <= tolerance, "$value should be equal to $expected")
    }
  }

  infix fun plusOrMinus(tolerance: Double): ToleranceMatcher = ToleranceMatcher(expected, tolerance)
}