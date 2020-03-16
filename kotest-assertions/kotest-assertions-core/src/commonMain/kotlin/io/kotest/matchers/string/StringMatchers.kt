package io.kotest.matchers.string

import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import kotlin.math.min

fun startWith(prefix: String) = neverNullMatcher<String> { value ->
  val ok = value.startsWith(prefix)
  var msg = "${value.show().value} should start with ${prefix.show().value}"
  val notmsg = "${value.show().value} should not start with ${prefix.show().value}"
  if (!ok) {
    for (k in 0 until min(value.length, prefix.length)) {
      if (value[k] != prefix[k]) {
        msg = "$msg (diverged at index $k)"
        break
      }
    }
  }
  MatcherResult(ok, msg, notmsg)
}

fun haveSubstring(substr: String) = include(substr)

fun endWith(suffix: String) = neverNullMatcher<String> { value ->
  MatcherResult(
      value.endsWith(suffix),
      "${value.show().value} should end with ${suffix.show().value}",
      "${value.show().value} should not end with ${suffix.show().value}")
}

fun match(regex: Regex) = neverNullMatcher<String> { value ->
  MatcherResult(
      value.matches(regex),
      "${value.show().value} should match regex $regex",
      "${value.show().value} should not match regex $regex")
}

fun match(regex: String): Matcher<String?> = match(regex.toRegex())

fun strlen(length: Int): Matcher<String?> = haveLength(length)

fun haveLength(length: Int) = neverNullMatcher<String> { value ->
  MatcherResult(
      value.length == length,
      "${value.show().value} should have length $length, but instead was ${value.length}",
      "${value.show().value} should not have length $length")
}
