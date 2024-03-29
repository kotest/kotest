package io.kotest.matchers.char

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot


/**
 * Assert that [Char] is in given range [CharRange].
 * @see [shouldNotBeInRange]
 * @see [beInRange]
 * */
infix fun Char.shouldBeInRange(range: CharRange): Char {
   this should beInRange(range)
   return this
}

/**
 * Assert that [Char] is not in given range [CharRange].
 * @see [shouldBeInRange]
 * @see [beInRange]
 * */
infix fun Char.shouldNotBeInRange(range: CharRange): Char {
   this shouldNot beInRange(range)
   return this
}

fun beInRange(range: CharRange) = object : Matcher<Char> {
   override fun test(value: Char): MatcherResult =
      MatcherResult(
         value in range,
         { "$value should be in range $range" },
         { "$value should not be in range $range" }
      )
}


/**
 * Assert that [Char] is in between from and to.
 * @see [shouldNotBeBetween]
 * @see [between]
 * */
fun Char.shouldBeBetween(from: Char, to: Char): Char {
   this should between(from, to)
   return this
}

/**
 * Assert that [Char] is not in between from and to.
 * @see [shouldBeBetween]
 * @see [between]
 * */
fun Char.shouldNotBeBetween(from: Char, to: Char): Char {
   this shouldNot between(from, to)
   return this
}

fun between(from: Char, to: Char) = object : Matcher<Char> {
   override fun test(value: Char) = MatcherResult(
      value in from..to,
      { "$value is between ($from, $to)" },
      { "$value is not between ($from, $to)" }
   )
}

/**
 * Assert that [Char] is equal to [other] ignoring case sensitivity
 * @see [shouldNotBeEqualToIgnoreCase]
 * @see [beEqualIgnoreCase]
 * */
infix fun Char.shouldBeEqualToIgnoreCase(other: Char): Char {
   this should beEqualIgnoreCase(other)
   return this
}

/**
 * Assert that [Char] is not equal to [other] ignoring case sensitivity
 * @see [shouldBeEqualToIgnoreCase]
 * @see [beEqualIgnoreCase]
 * */
infix fun Char.shouldNotBeEqualToIgnoreCase(other: Char): Char {
   this shouldNot beEqualIgnoreCase(other)
   return this
}

fun beEqualIgnoreCase(other: Char) = object : Matcher<Char> {
   override fun test(value: Char) = MatcherResult(
      value.equals(other, ignoreCase = true),
      { "$value should be equal ignoring case $other" }, {
         "$value should not be equal ignoring case $other"
      })
}
