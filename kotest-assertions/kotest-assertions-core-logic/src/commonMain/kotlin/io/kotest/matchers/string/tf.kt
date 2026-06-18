package io.kotest.matchers.string

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

/**
 * Assert that string should be truthy.
 *
 * Verifies that string is equal to one of the values: ["true", "yes", "y", "1"].
 * Assert is not case sensitive.
 *
 *
 * ```
 * "1".shouldBeTruthy()     // Assertion passes
 * "YeS".shouldBeTruthy()   // Assertion passes
 * "Y".shouldBeTruthy()     // Assertion passes
 *
 * "no".shouldBeTruthy()    // Assertion fails
 *
 * ```
 */
fun <A : CharSequence> A?.shouldBeTruthy(): A {
   this should beTruthy()
   return this!!
}

/**
 * Assert that string should be falsy.
 *
 * Verifies that string is equal to one of the values: ["false", "no", "n", "0"].
 * Assert is not case sensitive.
 *
 *
 * ```
 * "0".shouldBeFalsy()     // Assertion passes
 * "No".shouldBeFalsy()    // Assertion passes
 * "n".shouldBeFalsy()     // Assertion passes
 *
 * "yes".shouldBeFalsy()   // Assertion fails
 *
 * ```
 */
fun <A : CharSequence> A?.shouldBeFalsy(): A {
   this should beFalsy()
   return this!!
}

private val truthyValues = listOf("true", "yes", "y", "1")
private val falsyValues = listOf("false", "no", "n", "0")

/**
 * Matcher checks that string is truthy.
 *
 * Verifies that this string is equal to one of the values: ["true", "yes", "y", "1"].
 * Matcher is not case sensitive.
 *
 *
 * ```
 * "1" should beTruthy()       // Assertion passes
 * "YeS" should beTruthy()     // Assertion passes
 * "Y" should beTruthy()       // Assertion passes
 *
 * "no" should beTruthy()      // Assertion fails
 * "yes" shouldNot beTruthy()  // Assertion fails
 *
 * ```
 */
fun beTruthy(): Matcher<CharSequence?> = object : Matcher<CharSequence?> {
   override fun test(value: CharSequence?) = MatcherResult(
      truthyValues.any { it.equals(value?.toString(), ignoreCase = true) },
      { """${value.print().value} should be equal (ignoring case) to one of: $truthyValues""" },
      { """${value.print().value} should not be equal (ignoring case) to one of: $truthyValues""" }
   )
}

/**
 * Matcher checks that string is falsy.
 *
 * Verifies that this string is equal to one of the values: ["false", "no", "n", "0"].
 * Matcher is not case sensitive.
 *
 *
 * ```
 * "0" should beFalsy()     // Assertion passes
 * "No" should beFalsy()    // Assertion passes
 * "n" should beFalsy()     // Assertion passes
 *
 * "yes" should beFalsy()   // Assertion fails
 * "no" shouldNot beFalsy() // Assertion fails
 *
 * ```
 */
fun beFalsy(): Matcher<CharSequence?> = object : Matcher<CharSequence?> {
   override fun test(value: CharSequence?): MatcherResult {
      return MatcherResult(
         falsyValues.any { it.equals(value?.toString(), ignoreCase = true) },
         { """${value.print().value} should be equal (ignoring case) to one of: $falsyValues""" },
         { """${value.print().value} should not be equal (ignoring case) to one of: $falsyValues""" }
      )
   }
}
