package io.kotest.matchers.ints

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe

fun UInt.shouldBeBetween(lower: UInt, upper: UInt): UInt {
   this shouldBe between(lower, upper)
   return this
}

fun between(lower: UInt, upper: UInt) = object : Matcher<UInt> {
   override fun test(value: UInt) = MatcherResult(
      value in lower..upper,
      "$value should be between ($lower, $upper) inclusive",
      "$value should not be between ($lower, $upper) inclusive"
   )
}
