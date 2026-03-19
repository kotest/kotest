package io.kotest.matchers.doubles

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

/**
 * Beware, has no tolerance handling so will fail for numbers where the orders of magnitude vary greatly.
 */
infix fun Double?.shouldBeMultipleOf(other: Double): Double? {
   this should beMultipleOf(other)
   return this
}

/**
 * Beware, has no tolerance handling so will fail for numbers where the orders of magnitude vary greatly.
 */
fun beMultipleOf(other: Double) = Matcher<Double?> { value ->
   MatcherResult(
      value != null && value % other == 0.0,
      { "$value should be multiple of $other" },
      { "$value should not be multiple of $other" }
   )
}
