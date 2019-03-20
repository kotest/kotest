package io.kotlintest.matchers

import convertValueToString
import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.matchers.string.include
import io.kotlintest.neverNullMatcher
import kotlin.math.min

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
