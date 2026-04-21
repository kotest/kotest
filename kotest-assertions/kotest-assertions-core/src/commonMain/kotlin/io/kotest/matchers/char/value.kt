package io.kotest.matchers.char

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Asserts that this [Char] is a letter.
 * @see [shouldNotBeLetter]
 * @see [beLetter]
 */
fun Char?.shouldBeLetter(): Char {
   contract {
      returns() implies (this@shouldBeLetter != null)
   }

   this should beLetter()
   return this!!
}

/**
 * Asserts that this [Char] is not a letter.
 * @see [shouldBeLetter]
 * @see [beLetter]
 */
fun Char?.shouldNotBeLetter(): Char {
   contract {
      returns() implies (this@shouldNotBeLetter != null)
   }

   this shouldNot beLetter()
   return this!!
}

/**
 * Matcher that verifies a given [Char]? is a letter.
 * @see [beDigit]
 * @see [beLetterOrDigit]
 * @see [beWhitespace]
 */
fun beLetter(): Matcher<Char?> = charValueMatcher("a letter") { it.isLetter() }

/**
 * Asserts that this [Char] is a letter or digit.
 * @see [shouldNotBeLetterOrDigit]
 * @see [beLetterOrDigit]
 */
fun Char?.shouldBeLetterOrDigit(): Char {
   contract {
      returns() implies (this@shouldBeLetterOrDigit != null)
   }

   this should beLetterOrDigit()
   return this!!
}

/**
 * Asserts that this [Char] is not a letter or digit.
 * @see [shouldBeLetterOrDigit]
 * @see [beLetterOrDigit]
 */
fun Char?.shouldNotBeLetterOrDigit(): Char {
   contract {
      returns() implies (this@shouldNotBeLetterOrDigit != null)
   }

   this shouldNot beLetterOrDigit()
   return this!!
}

/**
 * Matcher that verifies a given [Char]? is a letter or digit.
 * @see [beDigit]
 * @see [beLetter]
 * @see [beWhitespace]
 */
fun beLetterOrDigit(): Matcher<Char?> = charValueMatcher("a letter or digit") { it.isLetterOrDigit() }

/**
 * Asserts that this [Char] is a digit.
 * @see [shouldNotBeDigit]
 * @see [beDigit]
 */
fun Char?.shouldBeDigit(): Char {
   contract {
      returns() implies (this@shouldBeDigit != null)
   }

   this should beDigit()
   return this!!
}

/**
 * Asserts that this [Char] is not a digit.
 * @see [shouldBeDigit]
 * @see [beDigit]
 */
fun Char?.shouldNotBeDigit(): Char {
   contract {
      returns() implies (this@shouldNotBeDigit != null)
   }

   this shouldNot beDigit()
   return this!!
}

/**
 * Matcher that verifies a given [Char]? is a digit.
 * @see [beLetter]
 * @see [beLetterOrDigit]
 * @see [beWhitespace]
 */
fun beDigit(): Matcher<Char?> = charValueMatcher("a digit") { it.isDigit() }

/**
 * Asserts that this [Char] is whitespace.
 * @see [shouldNotBeWhitespace]
 * @see [beWhitespace]
 */
fun Char?.shouldBeWhitespace(): Char {
   contract {
      returns() implies (this@shouldBeWhitespace != null)
   }

   this should beWhitespace()
   return this!!
}

/**
 * Asserts that this [Char] is not whitespace.
 * @see [shouldBeWhitespace]
 * @see [beWhitespace]
 */
fun Char?.shouldNotBeWhitespace(): Char {
   contract {
      returns() implies (this@shouldNotBeWhitespace != null)
   }

   this shouldNot beWhitespace()
   return this!!
}

/**
 * Matcher that verifies a given [Char]? is whitespace.
 * @see [beDigit]
 * @see [beLetter]
 * @see [beLetterOrDigit]
 */
fun beWhitespace(): Matcher<Char?> = charValueMatcher("whitespace") { it.isWhitespace() }

private inline fun charValueMatcher(valueDescription: String, crossinline predicate: (Char) -> Boolean): Matcher<Char?> = neverNullMatcher { value ->
   MatcherResult(
      predicate(value),
      { "${value.print().value} should be $valueDescription" },
      { "${value.print().value} should not be $valueDescription" }
   )
}
