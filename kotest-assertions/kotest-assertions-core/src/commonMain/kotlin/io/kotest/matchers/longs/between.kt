package io.kotest.matchers.longs

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot

fun Long.shouldBeBetween(a: Long, b: Long) = this shouldBe between(a, b)
fun Long.shouldNotBeBetween(a: Long, b: Long) = this shouldNot between(a, b)

fun between(a: Long, b: Long): Matcher<Long> = object : Matcher<Long> {
   override fun test(value: Long) = MatcherResult(value in a..b,
      "$value should be between ($a, $b)",
      "$value should not be between ($a, $b)")
}
