package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.matchers.doubles.ToleranceMatcher
import io.kotlintest.matchers.doubles.between
import io.kotlintest.matchers.doubles.exactly
import io.kotlintest.matchers.doubles.plusOrMinus

@Deprecated("This method was moved to another package, and will be removed in a future update", ReplaceWith("plusOrMinus(tolerance)", "io.kotlintest.matchers.doubles.plusOrMinus"))
infix fun Double.plusOrMinus(tolerance: Double) = plusOrMinus(tolerance)

@Deprecated("This method was moved to another package, and will be removed in a future update", ReplaceWith("exactly(d)", "io.kotlintest.matchers.doubles.exactly"))
fun exactly(d: Double) = exactly(d)

@Deprecated("This method was moved to another package, and will be removed in a future update", ReplaceWith("between(a, b, tolerance)", "io.kotlintest.matchers.doubles.between"))
fun between(a: Double, b: Double, tolerance: Double) = between(a, b, tolerance)

@Deprecated("This class was moved to another package, and will be removed in a future update", ReplaceWith("ToleranceMatcher(expected, tolerance)", "io.kotlintest.matchers.doubles.ToleranceMatcher"))
class ToleranceMatcher(private val expected: Double?, private val tolerance: Double) : Matcher<Double?> by ToleranceMatcher(expected, tolerance)
