package io.kotest.matchers.char

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.comparables.between
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
 * Assert that [Char] is in between [from, to] (inclusive, inclusive).
 *
 * @see [shouldNotBeBetween]
 * @see [between]
 * */
@Deprecated(
   "Char-specific assertion is getting replaced with a new Comparable assertion of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Char import `io.kotest.matchers.char.shouldBeBetween` manually.",
   ReplaceWith("shouldBeBetween(from, to)", "io.kotest.matchers.comparables.shouldBeBetween")
)
fun Char.shouldBeBetween(from: Char, to: Char): Char {
   this should between(from, to)
   return this
}

/**
 * Assert that [Char] is not between [from, to] (inclusive, inclusive).
 *
 * @see [shouldBeBetween]
 * @see [between]
 * */
@Deprecated(
   "Char-specific assertion is getting replaced with a new Comparable assertion of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Char import `io.kotest.matchers.char.shouldNotBeBetween` manually.",
   ReplaceWith("shouldNotBeBetween(from, to)", "io.kotest.matchers.comparables.shouldNotBeBetween")
)
fun Char.shouldNotBeBetween(from: Char, to: Char): Char {
   this shouldNot between(from, to)
   return this
}

@Deprecated(
   "Char-specific matcher is getting replaced with a new Comparable matcher of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Char import `io.kotest.matchers.char.between` manually.",
   ReplaceWith("between(from, to)", "io.kotest.matchers.comparables.between")
)
fun between(from: Char, to: Char): Matcher<Char> = between(from, to)

/**
 * Assert that [Char] is equal to [other] ignoring case sensitivity
 *
 * @see [shouldNotBeEqualToIgnoreCase]
 * @see [beEqualIgnoreCase]
 * */
infix fun Char.shouldBeEqualToIgnoreCase(other: Char): Char {
   this should beEqualIgnoreCase(other)
   return this
}

/**
 * Assert that [Char] is not equal to [other] ignoring case sensitivity
 *
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
