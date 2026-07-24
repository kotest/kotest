package io.kotest.matchers.kotlinx.datetime

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import kotlinx.datetime.Instant

/**
 * Assert that [Instant] is before [anotherInstant].
 * @see [shouldNotBeBefore]
 * */
@IgnorableReturnValue
infix fun Instant.shouldBeBefore(anotherInstant: Instant) = this shouldBe before(anotherInstant)

/**
 * Assert that [Instant] is not before [anotherInstant].
 * @see [shouldBeBefore]
 * */
@IgnorableReturnValue
infix fun Instant.shouldNotBeBefore(anotherInstant: Instant) = this shouldNotBe before(anotherInstant)

fun before(anotherInstant: Instant) = object : Matcher<Instant> {
    override fun test(value: Instant): MatcherResult {
        return MatcherResult(
            value < anotherInstant,
           { "Expected $value to be before $anotherInstant, but it's not." },
           { "$value is not expected to be before $anotherInstant." }
        )
    }
}

/**
 * Assert that [Instant] is after [anotherInstant].
 * @see [shouldNotBeAfter]
 * */
@IgnorableReturnValue
infix fun Instant.shouldBeAfter(anotherInstant: Instant) = this shouldBe after(anotherInstant)

/**
 * Assert that [Instant] is not after [anotherInstant].
 * @see [shouldNotBeAfter]
 * */
@IgnorableReturnValue
infix fun Instant.shouldNotBeAfter(anotherInstant: Instant) = this shouldNot after(anotherInstant)

fun after(anotherInstant: Instant) = object : Matcher<Instant> {
    override fun test(value: Instant): MatcherResult {
        return MatcherResult(
            value > anotherInstant,
           { "Expected $value to be after $anotherInstant, but it's not." },
           { "$value is not expected to be after $anotherInstant." }
        )
    }
}

/**
 * Assert that [Instant] is between [fromInstant] and [toInstant].
 * @see [shouldNotBeBetween]
 * */
@IgnorableReturnValue
fun Instant.shouldBeBetween(fromInstant: Instant, toInstant: Instant) = this shouldBe between(fromInstant, toInstant)

/**
 * Assert that [Instant] is between [fromInstant] and [toInstant].
 * @see [shouldBeBetween]
 * */
@IgnorableReturnValue
fun Instant.shouldNotBeBetween(fromInstant: Instant, toInstant: Instant) = this shouldNotBe between(fromInstant, toInstant)

fun between(fromInstant: Instant, toInstant: Instant) = object : Matcher<Instant> {
    override fun test(value: Instant): MatcherResult {
        return MatcherResult(
            value > fromInstant && value < toInstant,
            { "$value should be after $fromInstant and before $toInstant" },
            { "$value should not be after $fromInstant and before $toInstant" }
        )
    }
}
