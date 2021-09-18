package io.kotest.matchers.bytes

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe

fun Byte.shouldBeBetween(lower: Byte, upper: Byte): Byte {
   this shouldBe between(lower, upper)
   return this
}

fun between(lower: Byte, upper: Byte) = object : Matcher<Byte> {
   override fun test(value: Byte) = MatcherResult(
      value in lower..upper,
      { "$value should be between ($lower, $upper) inclusive" },
      { "$value should not be between ($lower, $upper) inclusive" }
   )
}
