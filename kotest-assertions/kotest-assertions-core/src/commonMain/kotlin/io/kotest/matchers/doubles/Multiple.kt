package io.kotest.matchers.doubles

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

fun beMultipleOf(other: Double) = Matcher<Double> { value ->
   MatcherResult(
      value.rem(other) == 0.0,
      { "${value} should be multiple of ${other}" },
      { "${value} should not be multiple of ${other}" }
   )
}
