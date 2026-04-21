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
 * Asserts that this [Char] is uppercase.
 * @see [shouldNotBeUpperCaseChar]
 * @see [beUpperCaseChar]
 */
@OptIn(ExperimentalContracts::class)
fun Char?.shouldBeUpperCaseChar(): Char {
   contract {
      returns() implies (this@shouldBeUpperCaseChar != null)
   }

   this should beUpperCaseChar()
   return this!!
}

/**
 * Asserts that this [Char] is not uppercase.
 * @see [shouldBeUpperCaseChar]
 * @see [beUpperCaseChar]
 */
@OptIn(ExperimentalContracts::class)
fun Char?.shouldNotBeUpperCaseChar(): Char {
   contract {
      returns() implies (this@shouldNotBeUpperCaseChar != null)
   }

   this shouldNot beUpperCaseChar()
   return this!!
}

/**
 * Matcher that verifies a given [Char]? is uppercase.
 * @see [beLowerCaseChar]
 * @see [beTitleCaseChar]
 */
fun beUpperCaseChar(): Matcher<Char?> = charCaseMatcher("upper") { it.uppercaseChar() }

/**
 * Asserts that this [Char] is lowercase.
 * @see [shouldNotBeLowerCaseChar]
 * @see [beLowerCaseChar]
 */
@OptIn(ExperimentalContracts::class)
fun Char?.shouldBeLowerCaseChar(): Char {
   contract {
      returns() implies (this@shouldBeLowerCaseChar != null)
   }

   this should beLowerCaseChar()
   return this!!
}

/**
 * Asserts that this [Char] is not lowercase.
 * @see [shouldBeLowerCaseChar]
 * @see [beLowerCaseChar]
 */
@OptIn(ExperimentalContracts::class)
fun Char?.shouldNotBeLowerCaseChar(): Char {
   contract {
      returns() implies (this@shouldNotBeLowerCaseChar != null)
   }

   this shouldNot beLowerCaseChar()
   return this!!
}

/**
 * Matcher that verifies a given [Char]? is lowercase.
 * @see [beTitleCaseChar]
 * @see [beUpperCaseChar]
 */
fun beLowerCaseChar(): Matcher<Char?> = charCaseMatcher("lower") { it.lowercaseChar() }

/**
 * Asserts that this [Char] is title case.
 * @see [shouldNotBeTitleCaseChar]
 * @see [beTitleCaseChar]
 */
@OptIn(ExperimentalContracts::class)
fun Char?.shouldBeTitleCaseChar(): Char {
   contract {
      returns() implies (this@shouldBeTitleCaseChar != null)
   }

   this should beTitleCaseChar()
   return this!!
}

/**
 * Asserts that this [Char] is not title case.
 * @see [shouldBeTitleCaseChar]
 * @see [beTitleCaseChar]
 */
@OptIn(ExperimentalContracts::class)
fun Char?.shouldNotBeTitleCaseChar(): Char {
   contract {
      returns() implies (this@shouldNotBeTitleCaseChar != null)
   }

   this shouldNot beTitleCaseChar()
   return this!!
}

/**
 * Matcher that verifies a given [Char]? is title case.
 * @see [beLowerCaseChar]
 * @see [beUpperCaseChar]
 */
fun beTitleCaseChar(): Matcher<Char?> = charCaseMatcher("title") { it.titlecaseChar() }

private inline fun charCaseMatcher(caseName: String, crossinline caseMap: (Char) -> Char): Matcher<Char?> = neverNullMatcher { value ->
   MatcherResult(
      caseMap(value) == value,
      { "${value.print().value} should be $caseName case" },
      { "${value.print().value} should not be $caseName case" }
   )
}
