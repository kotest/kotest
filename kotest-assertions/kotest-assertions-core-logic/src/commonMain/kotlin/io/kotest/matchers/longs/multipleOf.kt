package io.kotest.matchers.longs

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

infix fun Long?.shouldBeMultipleOf(other: Long): Long? {
   this should beMultipleOf(other)
   return this
}

fun beMultipleOf(other: Long) = Matcher<Long?> { value ->
   MatcherResult(
      value != null && value % other == 0L,
      { "${value} should be multiple of ${other}" },
      { "${value} should not be multiple of ${other}" }
   )
}
