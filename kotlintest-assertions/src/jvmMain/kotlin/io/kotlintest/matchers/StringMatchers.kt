package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.MatcherResult
import io.kotlintest.matchers.string.include
import io.kotlintest.neverNullMatcher
import io.kotlintest.show.show
import kotlin.math.min

fun startWith(prefix: String) = neverNullMatcher<String> { value ->
  val ok = value.startsWith(prefix)
  var msg = "${value.show()} should start with $prefix"
  val notmsg = "${value.show()} should not start with $prefix"
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
      "${value.show()} should end with $suffix",
      "${value.show()} should not end with $suffix")
}

fun match(regex: Regex) = neverNullMatcher<String> { value ->
  MatcherResult(
      value.matches(regex),
      "${value.show()} should match regex $regex",
      "${value.show()} should not match regex $regex")
}

fun match(regex: String): Matcher<String?> = match(regex.toRegex())

fun strlen(length: Int): Matcher<String?> = haveLength(length)

fun haveLength(length: Int) = neverNullMatcher<String> { value ->
  MatcherResult(
      value.length == length,
      "${value.show()} should have length $length, but instead was ${value.length}",
      "${value.show()} should not have length $length")
}
