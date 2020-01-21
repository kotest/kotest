package io.kotest.matchers.char

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot

infix fun Char.shouldBeInRange(range: CharRange) = this should beInRange(range)
infix fun Char.shouldNotBeInRange(range: CharRange) = this shouldNot beInRange(range)
fun beInRange(range: CharRange) = object : Matcher<Char> {
   override fun test(value: Char): MatcherResult =
      MatcherResult(
         value in range,
         "$value should be in range $range",
         "$value should not be in range $range"
      )
}


fun Char.shouldBeBetween(from: Char, to: Char) = this should between(from, to)
fun Char.shouldNotBeBetween(from: Char, to: Char) = this shouldNot between(from, to)
fun between(from: Char, to: Char): Matcher<Char> = object : Matcher<Char> {
   override fun test(value: Char) = MatcherResult(value in from..to,
      "$value is between ($from, $to)",
      "$value is not between ($from, $to)")
}
