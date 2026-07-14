package io.kotest.matchers.char

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Asserts that this [Char] is uppercase.
 * @see [shouldNotBeUpperCaseChar]
 * @see [beUpperCaseChar]
 */
@IgnorableReturnValue
fun Char.shouldBeUpperCaseChar(): Char {
   this should beUpperCaseChar()
   return this
}

/**
 * Asserts that this [Char] is not uppercase.
 * @see [shouldBeUpperCaseChar]
 * @see [beUpperCaseChar]
 */
@IgnorableReturnValue
fun Char.shouldNotBeUpperCaseChar(): Char {
   this shouldNot beUpperCaseChar()
   return this
}

/**
 * Matcher that verifies a given [Char] is uppercase.
 * @see [beLowerCaseChar]
 * @see [beTitleCaseChar]
 */
fun beUpperCaseChar(): Matcher<Char> = charCaseMatcher("upper") { it.uppercaseChar() }

/**
 * Asserts that this [Char] is lowercase.
 * @see [shouldNotBeLowerCaseChar]
 * @see [beLowerCaseChar]
 */
@IgnorableReturnValue
fun Char.shouldBeLowerCaseChar(): Char {
   this should beLowerCaseChar()
   return this
}

/**
 * Asserts that this [Char] is not lowercase.
 * @see [shouldBeLowerCaseChar]
 * @see [beLowerCaseChar]
 */
@IgnorableReturnValue
fun Char.shouldNotBeLowerCaseChar(): Char {
   this shouldNot beLowerCaseChar()
   return this
}

/**
 * Matcher that verifies a given [Char] is lowercase.
 * @see [beTitleCaseChar]
 * @see [beUpperCaseChar]
 */
fun beLowerCaseChar(): Matcher<Char> = charCaseMatcher("lower") { it.lowercaseChar() }

/**
 * Asserts that this [Char] is title case.
 * @see [shouldNotBeTitleCaseChar]
 * @see [beTitleCaseChar]
 */
@IgnorableReturnValue
fun Char.shouldBeTitleCaseChar(): Char {
   this should beTitleCaseChar()
   return this
}

/**
 * Asserts that this [Char] is not title case.
 * @see [shouldBeTitleCaseChar]
 * @see [beTitleCaseChar]
 */
@IgnorableReturnValue
fun Char.shouldNotBeTitleCaseChar(): Char {
   this shouldNot beTitleCaseChar()
   return this
}

/**
 * Matcher that verifies a given [Char] is title case.
 * @see [beLowerCaseChar]
 * @see [beUpperCaseChar]
 */
fun beTitleCaseChar(): Matcher<Char> = charCaseMatcher("title") { it.titlecaseChar() }

private inline fun charCaseMatcher(caseName: String, crossinline caseMap: (Char) -> Char): Matcher<Char> = object : Matcher<Char> {
   override fun test(value: Char) = MatcherResult(
      caseMap(value) == value,
      { "${value.print().value} should be $caseName case" },
      { "${value.print().value} should not be $caseName case" }
   )
}
