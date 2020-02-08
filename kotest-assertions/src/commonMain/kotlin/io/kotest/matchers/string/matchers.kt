package io.kotest.matchers.string

import io.kotest.assertions.show.show
import io.kotest.matchers.*
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.string.UUIDVersion.ANY
import kotlin.text.RegexOption.IGNORE_CASE

fun String?.shouldContainOnlyDigits() = this should containOnlyDigits()
fun String?.shouldNotContainOnlyDigits() = this shouldNot containOnlyDigits()

@UseExperimental(ExperimentalStdlibApi::class)
fun containOnlyDigits() = neverNullMatcher<String> { value ->
  MatcherResult(
    value.toCharArray().all { it in '0'..'9' },
    "${value.show()} should contain only digits",
    "${value.show()} should not contain only digits")
}

fun String?.shouldContainADigit() = this should containADigit()
fun String?.shouldNotContainADigit() = this shouldNot containADigit()

@UseExperimental(ExperimentalStdlibApi::class)
fun containADigit() = neverNullMatcher<String> { value ->
  MatcherResult(
    value.toCharArray().any { it in '0'..'9' },
    "${value.show()} should contain at least one digit",
    "${value.show()} should not contain any digits")
}

infix fun String?.shouldContainOnlyOnce(substr: String) = this should containOnlyOnce(
  substr)

infix fun String?.shouldNotContainOnlyOnce(substr: String) = this shouldNot containOnlyOnce(
  substr)
fun containOnlyOnce(substring: String) = neverNullMatcher<String> { value ->
  MatcherResult(
    value.indexOf(substring) >= 0 && value.indexOf(substring) == value.lastIndexOf(substring),
    "${value.show()} should contain the substring ${substring.show()} exactly once",
    "${value.show()} should not contain the substring ${substring.show()} exactly once"
  )
}

fun String?.shouldBeLowerCase() = this should beLowerCase()
fun String?.shouldNotBeLowerCase() = this shouldNot beLowerCase()
fun beLowerCase() = neverNullMatcher<String> { value ->
  MatcherResult(
    value.toLowerCase() == value,
    "${value.show()} should be lower case",
    "${value.show()} should not should be lower case")
}

fun String?.shouldBeUpperCase() = this should beUpperCase()
fun String?.shouldNotBeUpperCase() = this shouldNot beUpperCase()
fun beUpperCase() = neverNullMatcher<String> { value ->
  MatcherResult(
    value.toUpperCase() == value,
    "${value.show()} should be upper case",
    "${value.show()} should not should be upper case")
}

fun String?.shouldBeEmpty() = this should beEmpty()
fun String?.shouldNotBeEmpty() = this shouldNot beEmpty()
fun beEmpty() = neverNullMatcher<String> { value ->
  MatcherResult(
    value.isEmpty(),
    "${value.show()} should be empty",
    "${value.show()} should not be empty")
}

infix fun String?.shouldHaveSameLengthAs(other: String) = this should haveSameLengthAs(
  other)

infix fun String?.shouldNotHaveSameLengthAs(other: String) = this shouldNot haveSameLengthAs(
  other)
fun haveSameLengthAs(other: String) = neverNullMatcher<String> { value ->
  MatcherResult(
    value.length == other.length,
    "${value.show()} should have the same length as ${other.show()}",
    "${value.show()} should not have the same length as ${other.show()}")
}

fun String?.shouldBeSingleLine() = this should haveLineCount(1)
fun String?.shouldNotBeSingleLine() = this shouldNot haveLineCount(1)

infix fun String?.shouldHaveLineCount(count: Int) = this should haveLineCount(count)
infix fun String?.shouldNotHaveLineCount(count: Int) = this shouldNot haveLineCount(count)
/**
 * Match on the number of newlines in a string.
 *
 * This will count both "\n" and "\r\n", and so is not dependant on the system line separator.
 */
fun haveLineCount(count: Int) = neverNullMatcher<String> { value ->
  // plus one because we always have one more line than the new line character
  val lines = if (value.isEmpty()) 0 else value.count { it == '\n' } + 1
  MatcherResult(
    lines == count,
    { "${value.show()} should have $count lines but had $lines" },
    { "${value.show()} should not have $count lines" }
  )
}

fun String?.shouldBeBlank() = this should beBlank()
fun String?.shouldNotBeBlank() = this shouldNot beBlank()
fun containOnlyWhitespace() = beBlank()
fun beBlank() = neverNullMatcher<String> { value ->
  MatcherResult(
    value.isBlank(),
    { "${value.show()} should contain only whitespace" },
    { "${value.show()} should not contain only whitespace" }
  )
}

infix fun String?.shouldContainIgnoringCase(substr: String) = this should containIgnoringCase(
  substr)

infix fun String?.shouldNotContainIgnoringCase(substr: String) = this shouldNot containIgnoringCase(
  substr)
fun containIgnoringCase(substr: String) = neverNullMatcher<String> { value ->
  MatcherResult(
    value.toLowerCase().indexOf(substr.toLowerCase()) >= 0,
    { "${value.show()} should contain the substring ${substr.show()} (case insensitive)" },
    { "${value.show()} should not contain the substring ${substr.show()} (case insensitive)" }
  )
}

infix fun String?.shouldContain(regex: Regex) = this should contain(regex)
infix fun String?.shouldNotContain(regex: Regex) = this shouldNot contain(regex)
fun contain(regex: Regex) = neverNullMatcher<String> { value ->
  MatcherResult(
    value.contains(regex),
    { "${value.show()} should contain regex $regex" },
    { "${value.show()} should not contain regex $regex" })
}

fun String?.shouldContainInOrder(vararg substrings: String) = this should containInOrder(*substrings)
fun containInOrder(vararg substrings: String) = neverNullMatcher<String> { value ->
  val indexes = substrings.map { value.indexOf(it) }
  MatcherResult(
    indexes == indexes.sorted(),
    { "${value.show()} should include substrings ${substrings.show()} in order" },
    { "$value should not include substrings ${substrings.show()} in order" })
}

infix fun String?.shouldContain(substr: String) = this should contain(substr)
infix fun String?.shouldNotContain(substr: String) = this shouldNot contain(substr)
fun contain(substr: String) = include(substr)

infix fun String?.shouldInclude(substr: String) = this should include(substr)
infix fun String?.shouldNotInclude(substr: String) = this shouldNot include(substr)
fun include(substr: String) = neverNullMatcher<String> { value ->
  MatcherResult(
    value.contains(substr),
    "${value.show()} should include substring ${substr.show()}",
    "$value should not include substring ${substr.show()}")
}

infix fun String?.shouldHaveMaxLength(length: Int) = this should haveMaxLength(length)
infix fun String?.shouldNotHaveMaxLength(length: Int) = this shouldNot haveMaxLength(
  length)

fun haveMaxLength(length: Int) = neverNullMatcher<String> { value ->
  MatcherResult(
    value.length <= length,
    "${value.show()} should have maximum length of $length",
    "${value.show()} should have minimum length of ${length - 1}")
}

infix fun String?.shouldHaveMinLength(length: Int) = this should haveMinLength(length)
infix fun String?.shouldNotHaveMinLength(length: Int) = this shouldNot haveMinLength(
  length)

fun haveMinLength(length: Int) = neverNullMatcher<String> { value ->
  MatcherResult(
    value.length >= length,
    "${value.show()} should have minimum length of $length",
    "${value.show()} should have maximum length of ${length - 1}")
}


fun String?.shouldHaveLengthBetween(min: Int, max: Int) = this should haveLengthBetween(
  min,
  max)

fun String?.shouldNotHaveLengthBetween(min: Int, max: Int) = this shouldNot haveLengthBetween(
  min,
  max)

fun haveLengthBetween(min: Int, max: Int): Matcher<String?> {
  require(min <= max)
  return neverNullMatcher { value ->
    MatcherResult(
      value.length in min..max,
      "${value.show()} should have length in $min..$max but was ${value.length}",
      "${value.show()} should not have length between $min and $max")
  }
}


fun String?.shouldHaveLengthIn(range: IntRange) = this should haveLengthIn(range)
fun String?.shouldNotHaveLengthIn(range: IntRange) = this shouldNot haveLengthIn(range)

fun haveLengthIn(range: IntRange): Matcher<String?> {
  return neverNullMatcher { value ->
    MatcherResult(
      value.length in range,
      "${value.show()} should have length in $range but was ${value.length}",
      "${value.show()} should not have length between $range")
  }
}


infix fun String?.shouldHaveLength(length: Int) = this should haveLength(length)
infix fun String?.shouldNotHaveLength(length: Int) = this shouldNot haveLength(length)
infix fun String?.shouldMatch(regex: String) = this should match(regex)
infix fun String?.shouldMatch(regex: Regex) = this should match(regex)
infix fun String?.shouldNotMatch(regex: String) = this shouldNot match(regex)
infix fun String?.shouldEndWith(suffix: String) = this should endWith(suffix)
infix fun String?.shouldNotEndWith(suffix: String) = this shouldNot endWith(suffix)
infix fun String?.shouldStartWith(prefix: String) = this should startWith(prefix)
infix fun String?.shouldNotStartWith(prefix: String) = this shouldNot startWith(prefix)


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
infix fun String?.shouldBeEqualIgnoringCase(other: String) = this should beEqualIgnoringCase(
  other)

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
infix fun String?.shouldNotBeEqualIgnoringCase(other: String) = this shouldNot beEqualIgnoringCase(
  other)


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
    "${value.show()} should be equal ignoring case ${other.show()}",
    "${value.show()} should not be equal ignoring case ${other.show()}"
  )
}

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
fun String?.shouldBeTruthy() = this should beTruthy()

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
fun String?.shouldBeFalsy() = this should beFalsy()

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
      { """${value.show()} should be equal ignoring case one of values: $truthyValues""" },
      { """${value.show()} should not be equal ignoring case one of values: $truthyValues""" }
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
         { """${value.show()} should be equal ignoring case one of values: $falsyValues""" },
         { """${value.show()} should not be equal ignoring case one of values: $falsyValues""" }
     )
   }
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
) = this should beUUID(version, considerNilValid)

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
) = this shouldNot beUUID(version, considerNilValid)


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
