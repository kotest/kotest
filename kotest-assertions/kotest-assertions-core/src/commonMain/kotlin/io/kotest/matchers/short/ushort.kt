package io.kotest.matchers.short

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe

fun UShort.shouldBeBetween(lower: UShort, upper: UShort): UShort {
   this shouldBe between(lower, upper)
   return this
}

fun between(lower: UShort, upper: UShort) = object : Matcher<UShort> {
   override fun test(value: UShort) = MatcherResult(
      value in lower..upper,
      "$value should be between ($lower, $upper) inclusive",
      "$value should not be between ($lower, $upper) inclusive"
   )
}
