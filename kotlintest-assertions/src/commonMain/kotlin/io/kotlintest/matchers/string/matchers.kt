package io.kotlintest.matchers.string

import io.kotlintest.*
import kotlin.math.min

fun String?.shouldContainOnlyDigits() = this should containOnlyDigits()
fun String?.shouldNotContainOnlyDigits() = this shouldNot containOnlyDigits()
fun containOnlyDigits() = neverNullMatcher<String> { value ->
  Result(
          value.all { it.isDigit() },
      "${convertValueToString(value)} should contain only digits",
      "${convertValueToString(value)} should not contain only digits")
}

fun String?.shouldContainADigit() = this should containADigit()
fun String?.shouldNotContainADigit() = this shouldNot containADigit()
fun containADigit() = neverNullMatcher<String> { value ->
  Result(
          value.any { it.isDigit() },
      "${convertValueToString(value)} should contain at least one digit",
      "${convertValueToString(value)} should not contain any digits")
}

infix fun String?.shouldContainOnlyOnce(substr: String) = this should containOnlyOnce(substr)
infix fun String?.shouldNotContainOnlyOnce(substr: String) = this shouldNot containOnlyOnce(substr)
fun containOnlyOnce(substring: String) = neverNullMatcher<String> { value ->
  Result(
      value.indexOf(substring) >= 0 && value.indexOf(substring) == value.lastIndexOf(substring),
      "${convertValueToString(value)} should contain the substring ${convertValueToString(substring)} exactly once",
      "${convertValueToString(value)} should not contain the substring ${convertValueToString(substring)} exactly once"
  )
}

fun String?.shouldBeLowerCase() = this should beLowerCase()
fun String?.shouldNotBeLowerCase() = this shouldNot beLowerCase()
fun beLowerCase() = neverNullMatcher<String> { value ->
  Result(
      value.toLowerCase() == value,
      "${convertValueToString(value)} should be lower case",
      "${convertValueToString(value)} should not should be lower case")
}

fun String?.shouldBeUpperCase() = this should beUpperCase()
fun String?.shouldNotBeUpperCase() = this shouldNot beUpperCase()
fun beUpperCase() = neverNullMatcher<String> { value ->
  Result(
      value.toUpperCase() == value,
      "${convertValueToString(value)} should be upper case",
      "${convertValueToString(value)} should not should be upper case")
}

fun String?.shouldBeEmpty() = this should beEmpty()
fun String?.shouldNotBeEmpty() = this shouldNot beEmpty()
fun beEmpty() = neverNullMatcher<String> { value ->
  Result(
      value.isEmpty(),
      "${convertValueToString(value)} should be empty",
      "${convertValueToString(value)} should not be empty")
}

infix fun String?.shouldHaveSameLengthAs(other: String) = this should haveSameLengthAs(other)
infix fun String?.shouldNotHaveSameLengthAs(other: String) = this shouldNot haveSameLengthAs(other)
fun haveSameLengthAs(other: String) = neverNullMatcher<String> { value ->
  Result(
      value.length == other.length,
      "${convertValueToString(value)} should have the same length as ${convertValueToString(other)}",
      "${convertValueToString(value)} should not have the same length as ${convertValueToString(other)}")
}

infix fun String?.shouldHaveLineCount(count: Int) = this should haveLineCount(count)
infix fun String?.shouldNotHaveLineCount(count: Int) = this shouldNot haveLineCount(count)
/**
 * Match on the number of newlines in a string.
 *
 * This will count both "\n" and "\r\n", and so is not dependant on the system line separator.
 */
fun haveLineCount(count: Int) = neverNullMatcher<String> { value ->
  val lines = value.count { it == '\n' }
  Result(lines == count,
      "${convertValueToString(value)} should have $count lines but had $lines",
      "${convertValueToString(value)} should not have $count lines")
}

fun String?.shouldBeBlank() = this should beBlank()
fun String?.shouldNotBeBlank() = this shouldNot beBlank()
fun containOnlyWhitespace() = beBlank()
fun beBlank() = neverNullMatcher<String> { value ->
  Result(
      value.isBlank(),
      "${convertValueToString(value)} should contain only whitespace",
      "${convertValueToString(value)} should not contain only whitespace")
}

infix fun String?.shouldContainIgnoringCase(substr: String) = this should containIgnoringCase(substr)
infix fun String?.shouldNotContainIgnoringCase(substr: String) = this shouldNot containIgnoringCase(substr)
fun containIgnoringCase(substr: String) = neverNullMatcher<String> { value ->
  Result(
      value.toLowerCase().indexOf(substr.toLowerCase()) >= 0,
      "${convertValueToString(value)} should contain the substring ${convertValueToString(substr)} (case insensitive)",
      "${convertValueToString(value)} should not contain the substring ${convertValueToString(substr)} (case insensitive)")
}

infix fun String?.shouldContain(regex: Regex) = this should contain(regex)
infix fun String?.shouldNotContain(regex: Regex) = this shouldNot contain(regex)
fun contain(regex: Regex) = neverNullMatcher<String> { value ->
  Result(
      value.contains(regex),
      "${convertValueToString(value)} should contain regex $regex",
      "${convertValueToString(value)} should not contain regex $regex")
}

fun String?.shouldContainInOrder(vararg substrings: String) = this should containInOrder(*substrings)
fun containInOrder(vararg substrings: String) = neverNullMatcher<String> { value ->
  val indexes = substrings.map { value.indexOf(it) }
  Result(
      indexes == indexes.sorted(),
      "${convertValueToString(value)} should include substrings ${convertValueToString(substrings)} in order",
      "$value should not include substrings ${convertValueToString(substrings)} in order")
}

infix fun String?.shouldContain(substr: String) = this should contain(substr)
infix fun String?.shouldNotContain(substr: String) = this shouldNot contain(substr)
fun contain(substr: String) = include(substr)

infix fun String?.shouldInclude(substr: String) = this should include(substr)
infix fun String?.shouldNotInclude(substr: String) = this shouldNot include(substr)
fun include(substr: String) = neverNullMatcher<String> { value ->
  Result(
      value.contains(substr),
      "${convertValueToString(value)} should include substring ${convertValueToString(substr)}",
      "$value should not include substring ${convertValueToString(substr)}")
}

infix fun String?.shouldHaveMaxLength(length: Int) = this should haveMaxLength(length)
infix fun String?.shouldNotHaveMaxLength(length: Int) = this shouldNot haveMaxLength(length)

fun haveMaxLength(length: Int) = neverNullMatcher<String> { value ->
  Result(
      value.length <= length,
      "${convertValueToString(value)} should have maximum length of $length",
      "${convertValueToString(value)} should have minimum length of ${length - 1}")
}

infix fun String?.shouldHaveMinLength(length: Int) = this should haveMinLength(length)
infix fun String?.shouldNotHaveMinLength(length: Int) = this shouldNot haveMinLength(length)

fun haveMinLength(length: Int) = neverNullMatcher<String> { value ->
  Result(
      value.length >= length,
      "${convertValueToString(value)} should have minimum length of $length",
      "${convertValueToString(value)} should have maximum length of ${length - 1}")
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


fun startWith(prefix: String) = neverNullMatcher<String> { value ->
  val ok = value.startsWith(prefix)
  var msg = "${convertValueToString(value)} should start with $prefix"
  val notmsg = "${convertValueToString(value)} should not start with $prefix"
  if (!ok) {
    for (k in 0 until min(value.length, prefix.length)) {
      if (value[k] != prefix[k]) {
        msg = "$msg (diverged at index $k)"
        break
      }
    }
  }
  Result(ok, msg, notmsg)
}

fun haveSubstring(substr: String) = include(substr)

fun endWith(suffix: String) = neverNullMatcher<String> { value ->
  Result(
          value.endsWith(suffix),
          "${convertValueToString(value)} should end with $suffix",
          "${convertValueToString(value)} should not end with $suffix")
}

fun match(regex: Regex) = neverNullMatcher<String> { value ->
  Result(
          value.matches(regex),
          "${convertValueToString(value)} should match regex $regex",
          "${convertValueToString(value)} should not match regex $regex")
}

fun match(regex: String): Matcher<String?> = match(regex.toRegex())

fun strlen(length: Int): Matcher<String?> = haveLength(length)

fun haveLength(length: Int) = neverNullMatcher<String> { value ->
  Result(
          value.length == length,
          "${convertValueToString(value)} should have length $length",
          "${convertValueToString(value)} should not have length $length")
}



expect fun Char.isDigit(): Boolean