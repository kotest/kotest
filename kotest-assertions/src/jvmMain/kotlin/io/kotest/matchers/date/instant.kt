package io.kotest.matchers.date

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot
import java.time.Instant

fun beforeInstantMatcher(anotherInstant: Instant) = object : Matcher<Instant> {
   override fun test(value: Instant): MatcherResult {
      return MatcherResult(
         value.isBefore(anotherInstant),
         {"Expected $value to be before $anotherInstant, but it's not."},
         {"$anotherInstant is not expected to be before $value."}
      )
   }
}

fun afterInstantMatcher(anotherInstant: Instant) = object : Matcher<Instant> {
   override fun test(value: Instant): MatcherResult {
      return MatcherResult(
         value.isAfter(anotherInstant),
         {"Expected $value to be after $anotherInstant, but it's not."},
         {"$anotherInstant is not expected to be after $value."}
      )
   }
}

/**
 * Assert that [Instant] is before [anotherInstant].
 * @see [shouldNotBeBefore]
 * */
infix fun Instant.shouldBeBefore(anotherInstant: Instant) = this should beforeInstantMatcher(anotherInstant)

/**
 * Assert that [Instant] is not before [anotherInstant].
 * @see [shouldBeBefore]
 * */
infix fun Instant.shouldNotBeBefore(anotherInstant: Instant) = this shouldNot beforeInstantMatcher(anotherInstant)

/**
 * Assert that [Instant] is after [anotherInstant].
 * @see [shouldNotBeAfter]
 * */
infix fun Instant.shouldBeAfter(anotherInstant: Instant) = this should afterInstantMatcher(anotherInstant)

/**
 * Assert that [Instant] is not after [anotherInstant].
 * @see [shouldNotBeAfter]
 * */
infix fun Instant.shouldNotBeAfter(anotherInstant: Instant) = this shouldNot afterInstantMatcher(anotherInstant)
