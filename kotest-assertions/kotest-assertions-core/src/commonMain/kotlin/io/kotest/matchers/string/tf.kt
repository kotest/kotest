package io.kotest.matchers.string

import io.kotest.assertions.show.show
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
fun <A : CharSequence> A?.shouldBeTruthy(): A? {
   this.toString() should beTruthy()
   return this
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
fun <A : CharSequence> A?.shouldBeFalsy(): A? {
   this.toString() should beFalsy()
   return this
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
fun beTruthy() = object : Matcher<String?> {
   override fun test(value: String?) = MatcherResult(
      truthyValues.any { it.equals(value, ignoreCase = true) },
      { """${value.show().value} should be equal (ignoring case) to one of: $truthyValues""" },
      { """${value.show().value} should not be equal (ignoring case) to one of: $truthyValues""" }
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
fun beFalsy(): Matcher<String?> = object : Matcher<String?> {
   override fun test(value: String?): MatcherResult {
      return MatcherResult(
         falsyValues.any { it.equals(value, ignoreCase = true) },
         { """${value.show().value} should be equal (ignoring case) to one of: $falsyValues""" },
         { """${value.show().value} should not be equal (ignoring case) to one of: $falsyValues""" }
      )
   }
}
