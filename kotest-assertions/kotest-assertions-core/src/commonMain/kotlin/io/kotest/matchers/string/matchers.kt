package io.kotest.matchers.string

import io.kotest.assertions.failure
import io.kotest.assertions.show.show
import io.kotest.matchers.*
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.string.UUIDVersion.ANY
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.text.RegexOption.IGNORE_CASE

fun String?.shouldContainOnlyDigits(): String? {
   this should containOnlyDigits()
   return this
}

fun String?.shouldNotContainOnlyDigits(): String? {
   this shouldNot containOnlyDigits()
   return this
}

fun containOnlyDigits() = neverNullMatcher<String> { value ->
   MatcherResult(
      value.toCharArray().all { it in '0'..'9' },
      "${value.show().value} should contain only digits",
      "${value.show().value} should not contain only digits"
   )
}

fun String?.shouldContainADigit(): String? {
   this should containADigit()
   return this
}

fun String?.shouldNotContainADigit(): String? {
   this shouldNot containADigit()
   return this
}

fun containADigit() = neverNullMatcher<String> { value ->
   MatcherResult(
      value.toCharArray().any { it in '0'..'9' },
      "${value.show().value} should contain at least one digit",
      "${value.show().value} should not contain any digits"
   )
}

infix fun String?.shouldContainOnlyOnce(substr: String): String? {
   this should containOnlyOnce(substr)
   return this
}

infix fun String?.shouldNotContainOnlyOnce(substr: String): String? {
   this shouldNot containOnlyOnce(substr)
   return this
}

fun containOnlyOnce(substring: String) = neverNullMatcher<String> { value ->
   MatcherResult(
      value.indexOf(substring) >= 0 && value.indexOf(substring) == value.lastIndexOf(substring),
      "${value.show().value} should contain the substring ${substring.show().value} exactly once",
      "${value.show().value} should not contain the substring ${substring.show().value} exactly once"
   )
}

fun String?.shouldBeEmpty(): String? {
   this should beEmpty()
   return this
}

fun String?.shouldNotBeEmpty(): String? {
   this shouldNot beEmpty()
   return this
}

fun beEmpty() = neverNullMatcher<String> { value ->
   MatcherResult(
      value.isEmpty(),
      "${value.show().value} should be empty",
      "${value.show().value} should not be empty"
   )
}

fun String?.shouldBeBlank(): String? {
   this should beBlank()
   return this
}

fun String?.shouldNotBeBlank(): String? {
   this shouldNot beBlank()
   return this
}

fun containOnlyWhitespace() = beBlank()
fun beBlank() = neverNullMatcher<String> { value ->
   MatcherResult(
      value.isBlank(),
      { "${value.show().value} should contain only whitespace" },
      { "${value.show().value} should not contain only whitespace" }
   )
}

infix fun String?.shouldContainIgnoringCase(substr: String): String? {
   this should containIgnoringCase(substr)
   return this
}

infix fun String?.shouldNotContainIgnoringCase(substr: String): String? {
   this shouldNot containIgnoringCase(substr)
   return this
}

fun containIgnoringCase(substr: String) = neverNullMatcher<String> { value ->
   MatcherResult(
      value.toLowerCase().indexOf(substr.toLowerCase()) >= 0,
      { "${value.show().value} should contain the substring ${substr.show().value} (case insensitive)" },
      { "${value.show().value} should not contain the substring ${substr.show().value} (case insensitive)" }
   )
}

infix fun String?.shouldContain(regex: Regex): String? {
   this should contain(regex)
   return this
}

infix fun String?.shouldNotContain(regex: Regex): String? {
   this shouldNot contain(regex)
   return this
}

fun contain(regex: Regex) = neverNullMatcher<String> { value ->
   MatcherResult(
      value.contains(regex),
      { "${value.show().value} should contain regex $regex" },
      { "${value.show().value} should not contain regex $regex" })
}

fun String?.shouldContainInOrder(vararg substrings: String): String? {
   this should containInOrder(*substrings)
   return this
}

fun String?.shouldNotContainInOrder(vararg substrings: String): String? {
   this shouldNot containInOrder(*substrings)
   return this
}

fun containInOrder(vararg substrings: String) = neverNullMatcher<String> { value ->
   fun recTest(str: String, subs: List<String>): Boolean =
      subs.isEmpty() || str.indexOf(subs.first()).let { it > -1 && recTest(str.substring(it + 1), subs.drop(1)) }

   MatcherResult(
      recTest(value, substrings.filter { it.isNotEmpty() }),
      { "${value.show().value} should include substrings ${substrings.show().value} in order" },
      { "${value.show().value} should not include substrings ${substrings.show().value} in order" })
}

infix fun String?.shouldContain(substr: String): String? {
   this should contain(substr)
   return this
}

infix fun String?.shouldNotContain(substr: String): String? {
   this shouldNot contain(substr)
   return this
}

fun contain(substr: String) = include(substr)

infix fun String?.shouldInclude(substr: String): String? {
   this should include(substr)
   return this
}

infix fun String?.shouldNotInclude(substr: String): String? {
   this shouldNot include(substr)
   return this
}

fun include(substr: String) = neverNullMatcher<String> { value ->
   MatcherResult(
      value.contains(substr),
      "${value.show().value} should include substring ${substr.show().value}",
      "${value.show().value} should not include substring ${substr.show().value}"
   )
}

infix fun String?.shouldMatch(regex: String): String? {
   this should match(regex)
   return this
}

infix fun String?.shouldMatch(regex: Regex): String? {
   this should match(regex)
   return this
}

infix fun String?.shouldNotMatch(regex: String): String? {
   this shouldNot match(regex)
   return this
}

/**
 * Asserts that [this] is equal to [other] (ignoring case)
 *
 * Verifies that this string is equal to [other], ignoring case.
 * Opposite of [shouldNotBeEqualIgnoringCase]
 *
 * ```
 *  "foo" shouldBeEqualIgnoringCase "FoO"  // Assertion passes
 *
 *  "foo" shouldBeEqualIgnoringCase "BaR"  // Assertion fails
 * ```
 *
 * @see [shouldNotBeEqualIgnoringCase]
 * @see [beEqualIgnoringCase]
 */
infix fun String?.shouldBeEqualIgnoringCase(other: String): String? {
   this should beEqualIgnoringCase(other)
   return this
}

/**
 * Asserts that [this] is NOT equal to [other] (ignoring case)
 *
 * Verifies that this string is NOT equal to [other], ignoring case.
 * Opposite of [shouldBeEqualIgnoringCase]
 *
 * ```
 * "foo" shouldNotBeEqualIgnoringCase "FoO" // Assertion fails
 * "foo" shouldNotBeEqualIgnoringCase "foo" // Assertion fails
 *
 * "foo" shouldNotBeEqualIgnoringCase "bar" // Assertion passes
 *
 * ```
 *
 * @see [shouldBeEqualIgnoringCase]
 * @see [beEqualIgnoringCase]
 */
infix fun String?.shouldNotBeEqualIgnoringCase(other: String): String? {
   this shouldNot beEqualIgnoringCase(other)
   return this
}


/**
 * Matcher that matches strings that are equal when case is not considered
 *
 * Verifies that a specific String is equal to another String when case is not considered.
 *
 * ```
 * "foo" should beEqualIgnoringCase("FoO")   // Assertion passes
 *
 * "bar shouldNot beEqualIgnoringCase("BoB") // Assertion passes
 *
 * ```
 *
 */
fun beEqualIgnoringCase(other: String) = neverNullMatcher<String> { value ->
   MatcherResult(
      value.equals(other, ignoreCase = true),
      "${value.show().value} should be equal ignoring case ${other.show().value}",
      "${value.show().value} should not be equal ignoring case ${other.show().value}"
   )
}

enum class UUIDVersion(
   val uuidRegex: Regex
) {
   ANY("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex(IGNORE_CASE)),
   V1("[0-9a-f]{8}-[0-9a-f]{4}-[1][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex(IGNORE_CASE)),
   V2("[0-9a-f]{8}-[0-9a-f]{4}-[2][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex(IGNORE_CASE)),
   V3("[0-9a-f]{8}-[0-9a-f]{4}-[3][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex(IGNORE_CASE)),
   V4("[0-9a-f]{8}-[0-9a-f]{4}-[4][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex(IGNORE_CASE)),
   V5("[0-9a-f]{8}-[0-9a-f]{4}-[5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex(IGNORE_CASE));
}

/**
 * Asserts that this String is a valid UUID
 *
 * Opposite of [shouldNotBeUUID]
 *
 * Verifies that this string is a valid UUID as per RFC4122. Version depends on [version]. By default, all versions
 * (v1 through v5) are matched. A special attention is necessary for the NIL UUID (an UUID with all zeros),
 * which is considered a valid UUID. By default it's matched as valid.
 *
 * ```
 * "123e4567-e89b-12d3-a456-426655440000".shouldBeUUID(version = ANY)  // Assertion passes
 * "123e4567-e89b-12d3-a456-426655440000".shouldBeUUID(version = V4)  // Assertion Fails (is V1 UUID)
 * "123e4567e89b12d3a456426655440000".shouldBeUUID()      // Assertion fails
 * "00000000-0000-0000-0000-000000000000".shouldBeUUID(considerNilValid = true)  // Assertion passes
 *
 * ```
 *
 * @see [RFC4122] https://tools.ietf.org/html/rfc4122
 */
fun String.shouldBeUUID(
   version: UUIDVersion = ANY,
   considerNilValid: Boolean = true
): String {
   this should beUUID(version, considerNilValid)
   return this
}

/**
 * Asserts that this String is NOT a valid UUID
 *
 * Opposite of [shouldBeUUID]
 *
 * Verifies that this string is a NOT valid UUID as per RFC4122. Version depends on [version]. By default, all versions
 * (v1 through v5) are matched. A special attention is necessary for the NIL UUID (an UUID with all zeros),
 * which is considered a valid UUID. By default it's matched as valid.
 *
 * ```
 * "123e4567-e89b-12d3-a456-426655440000".shouldNotBeUUID(version = ANY)  // Assertion fails
 * "123e4567e89b12d3a456426655440000".shouldNotBeUUID()      // Assertion passes
 * "00000000-0000-0000-0000-000000000000".shouldNotBeUUID(considerNilValid = true)  // Assertion fails
 *
 * ```
 *
 * @see [RFC4122] https://tools.ietf.org/html/rfc4122
 */
fun String.shouldNotBeUUID(
   version: UUIDVersion = ANY,
   considerNilValid: Boolean = true
): String {
   this shouldNot beUUID(version, considerNilValid)
   return this
}


/**
 * Matcher that verifies if a String is an UUID
 *
 *
 * Verifies that a string is a valid UUID as per RFC4122. Version depends on [version]. By default, all versions
 * (v1 through v5) are matched. A special attention is necessary for the NIL UUID (an UUID with all zeros),
 * which is considered a valid UUID. By default it's matched as valid.
 *
 *
 * @see [RFC4122] https://tools.ietf.org/html/rfc4122
 * @see shouldBeUUID
 * @see shouldNotBeUUID
 */
fun beUUID(
   version: UUIDVersion = ANY,
   considerNilValid: Boolean = true
) = object : Matcher<String> {
   override fun test(value: String) = MatcherResult(
      value.matches(version.uuidRegex) || (considerNilValid && value.isNilUUID()),
      "String $value is not an UUID ($version), but should be",
      "String $value is an UUID ($version), but shouldn't be"
   )

   private fun String.isNilUUID() = this == "00000000-0000-0000-0000-000000000000"
}

@OptIn(ExperimentalContracts::class)
fun String?.shouldBeInteger(radix: Int = 10): Int {
   contract {
      returns() implies (this@shouldBeInteger != null)
   }

   return when (this) {
      null -> throw failure("String is null, but it should be integer.")
      else -> when (val integer = this.toIntOrNull(radix)) {
         null -> throw failure("String '$this' is not integer, but it should be.")
         else -> integer
      }
   }
}
