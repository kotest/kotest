package io.kotest.matchers.char

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Asserts that this [Char] is a letter.
 * @see [shouldNotBeLetter]
 * @see [beLetter]
 */
@IgnorableReturnValue
fun Char.shouldBeLetter(): Char {
   this should beLetter()
   return this
}

/**
 * Asserts that this [Char] is not a letter.
 * @see [shouldBeLetter]
 * @see [beLetter]
 */
@IgnorableReturnValue
fun Char.shouldNotBeLetter(): Char {
   this shouldNot beLetter()
   return this
}

/**
 * Matcher that verifies a given [Char] is a letter.
 * @see [beDigit]
 * @see [beLetterOrDigit]
 * @see [beWhitespace]
 */
fun beLetter(): Matcher<Char> = charValueMatcher("a letter") { it.isLetter() }

/**
 * Asserts that this [Char] is a letter or digit.
 * @see [shouldNotBeLetterOrDigit]
 * @see [beLetterOrDigit]
 */
@IgnorableReturnValue
fun Char.shouldBeLetterOrDigit(): Char {
   this should beLetterOrDigit()
   return this
}

/**
 * Asserts that this [Char] is not a letter or digit.
 * @see [shouldBeLetterOrDigit]
 * @see [beLetterOrDigit]
 */
@IgnorableReturnValue
fun Char.shouldNotBeLetterOrDigit(): Char {
   this shouldNot beLetterOrDigit()
   return this
}

/**
 * Matcher that verifies a given [Char] is a letter or digit.
 * @see [beDigit]
 * @see [beLetter]
 * @see [beWhitespace]
 */
fun beLetterOrDigit(): Matcher<Char> = charValueMatcher("a letter or digit") { it.isLetterOrDigit() }

/**
 * Asserts that this [Char] is a digit.
 * @see [shouldNotBeDigit]
 * @see [beDigit]
 */
@IgnorableReturnValue
fun Char.shouldBeDigit(): Char {
   this should beDigit()
   return this
}

/**
 * Asserts that this [Char] is not a digit.
 * @see [shouldBeDigit]
 * @see [beDigit]
 */
@IgnorableReturnValue
fun Char.shouldNotBeDigit(): Char {
   this shouldNot beDigit()
   return this
}

/**
 * Matcher that verifies a given [Char] is a digit.
 * @see [beLetter]
 * @see [beLetterOrDigit]
 * @see [beWhitespace]
 */
fun beDigit(): Matcher<Char> = charValueMatcher("a digit") { it.isDigit() }

/**
 * Asserts that this [Char] is whitespace.
 * @see [shouldNotBeWhitespace]
 * @see [beWhitespace]
 */
@IgnorableReturnValue
fun Char.shouldBeWhitespace(): Char {
   this should beWhitespace()
   return this
}

/**
 * Asserts that this [Char] is not whitespace.
 * @see [shouldBeWhitespace]
 * @see [beWhitespace]
 */
@IgnorableReturnValue
fun Char.shouldNotBeWhitespace(): Char {
   this shouldNot beWhitespace()
   return this
}

/**
 * Matcher that verifies a given [Char] is whitespace.
 * @see [beDigit]
 * @see [beLetter]
 * @see [beLetterOrDigit]
 */
fun beWhitespace(): Matcher<Char> = charValueMatcher("whitespace") { it.isWhitespace() }

private inline fun charValueMatcher(valueDescription: String, crossinline predicate: (Char) -> Boolean): Matcher<Char> = object : Matcher<Char> {
   override fun test(value: Char) = MatcherResult(
      predicate(value),
      { "${value.print().value} should be $valueDescription" },
      { "${value.print().value} should not be $valueDescription" }
   )
}
