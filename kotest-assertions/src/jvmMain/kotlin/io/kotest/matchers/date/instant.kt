package io.kotest.matchers.date

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import java.time.Instant

fun before(anotherInstant: Instant) = object : Matcher<Instant> {
   override fun test(value: Instant): MatcherResult {
      return MatcherResult(
         value.isBefore(anotherInstant),
         {"Expected $value to be before $anotherInstant, but it's not."},
         {"$anotherInstant is not expected to be before $value."}
      )
   }
}

fun after(anotherInstant: Instant) = object : Matcher<Instant> {
   override fun test(value: Instant): MatcherResult {
      return MatcherResult(
         value.isAfter(anotherInstant),
         {"Expected $value to be after $anotherInstant, but it's not."},
         {"$anotherInstant is not expected to be after $value."}
      )
   }
}

fun between(fromInstant: Instant, toInstant: Instant) = object : Matcher<Instant> {
   override fun test(value: Instant): MatcherResult {
      return MatcherResult(
         value.isAfter(fromInstant) && value.isBefore(toInstant),
         { "$value should be after $fromInstant and before $toInstant" },
         { "$value should not be be after $fromInstant and before $toInstant" }
      )
   }
}

/**
 * Assert that [Instant] is before [anotherInstant].
 * @see [shouldNotBeBefore]
 * */
infix fun Instant.shouldBeBefore(anotherInstant: Instant) = this shouldBe before(anotherInstant)

/**
 * Assert that [Instant] is not before [anotherInstant].
 * @see [shouldBeBefore]
 * */
infix fun Instant.shouldNotBeBefore(anotherInstant: Instant) = this shouldNotBe before(anotherInstant)

/**
 * Assert that [Instant] is after [anotherInstant].
 * @see [shouldNotBeAfter]
 * */
infix fun Instant.shouldBeAfter(anotherInstant: Instant) = this shouldBe after(anotherInstant)

/**
 * Assert that [Instant] is not after [anotherInstant].
 * @see [shouldNotBeAfter]
 * */
infix fun Instant.shouldNotBeAfter(anotherInstant: Instant) = this shouldNot after(anotherInstant)

/**
 * Assert that [Instant] is between [fromInstant] and [toInstant].
 * @see [shouldNotBeBetween]
 * */
fun Instant.shouldBeBetween(fromInstant: Instant, toInstant: Instant) = this shouldBe between(fromInstant, toInstant)

/**
 * Assert that [Instant] is between [fromInstant] and [toInstant].
 * @see [shouldBeBetween]
 * */
fun Instant.shouldNotBeBetween(fromInstant: Instant, toInstant: Instant) = this shouldNotBe between(fromInstant, toInstant)
