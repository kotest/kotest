package io.kotest.matchers.longs

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe

fun ULong.shouldBeBetween(lower: ULong, upper: ULong): ULong {
   this shouldBe between(lower, upper)
   return this
}

fun between(lower: ULong, upper: ULong) = object : Matcher<ULong> {
   override fun test(value: ULong) = MatcherResult(
      value in lower..upper,
      { "$value should be between ($lower, $upper) inclusive" },
      {
         "$value should not be between ($lower, $upper) inclusive"
      })
}
