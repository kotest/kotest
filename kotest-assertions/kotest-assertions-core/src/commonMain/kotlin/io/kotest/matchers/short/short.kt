package io.kotest.matchers.short

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe

fun Short.shouldBeBetween(lower: Short, upper: Short): Short {
   this shouldBe between(lower, upper)
   return this
}

fun between(lower: Short, upper: Short) = object : Matcher<Short> {
   override fun test(value: Short) = MatcherResult(
      value in lower..upper,
      "$value should be between ($lower, $upper) inclusive",
      "$value should not be between ($lower, $upper) inclusive"
   )
}
