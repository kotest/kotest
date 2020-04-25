package io.kotest.matchers.longs

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot

/**
 * Verifies that the given Long is between a and b inclusive.
 */
fun Long.shouldBeBetween(a: Long, b: Long) = this shouldBe between(a, b)

/**
 * Verifies that the given Long is NOT between a and b inclusive.
 */
fun Long.shouldNotBeBetween(a: Long, b: Long) = this shouldNot between(a, b)

/**
 * Verifies that the given Long is between a and b inclusive.
 */
fun between(a: Long, b: Long): Matcher<Long> = object : Matcher<Long> {
   override fun test(value: Long) = MatcherResult(value in a..b,
      "$value should be between ($a, $b)",
      "$value should not be between ($a, $b)")
}
