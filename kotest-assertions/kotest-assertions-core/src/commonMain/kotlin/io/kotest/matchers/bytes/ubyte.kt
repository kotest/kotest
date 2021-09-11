package io.kotest.matchers.bytes

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe

fun UByte.shouldBeBetween(lower: UByte, upper: UByte): UByte {
   this shouldBe between(lower, upper)
   return this
}

fun between(lower: UByte, upper: UByte) = object : Matcher<UByte> {
   override fun test(value: UByte) = MatcherResult(
      value in lower..upper,
      { "$value should be between ($lower, $upper) inclusive" },
      {
         "$value should not be between ($lower, $upper) inclusive"
      })
}
