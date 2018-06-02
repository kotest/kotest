package io.kotlintest.matchers

import convertValueToString
import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.matchers.string.include

fun startWith(prefix: String): Matcher<String> = object : Matcher<String> {
  override fun test(value: String): Result {
    val ok = value.startsWith(prefix)
    var msg = "${convertValueToString(value)} should start with $prefix"
    val notmsg = "${convertValueToString(value)} should not start with $prefix"
    if (!ok) {
      for (k in 0 until Math.min(value.length, prefix.length)) {
        if (value[k] != prefix[k]) {
          msg = "$msg (diverged at index $k)"
          break
        }
      }
    }
    return Result(ok, msg, notmsg)
  }
}

fun haveSubstring(substr: String) = include(substr)
@Deprecated("use should include(substring)", ReplaceWith("include(substr)", "io.kotlintest.matchers.string.include"))
fun substring(substr: String) = include(substr)

fun endWith(suffix: String): Matcher<String> = object : Matcher<String> {
  override fun test(value: String) = Result(value.endsWith(suffix),
      "${convertValueToString(value)} should end with $suffix",
      "${convertValueToString(value)} should not end with $suffix")
}

fun match(regex: Regex): Matcher<String> = object : Matcher<String> {
  override fun test(value: String) = Result(
      value.matches(regex),
      "${convertValueToString(value)} should match regex $regex",
      "${convertValueToString(value)} should not match regex $regex")
}

fun match(regex: String): Matcher<String> = match(regex.toRegex())

fun strlen(length: Int): Matcher<String> = haveLength(length)

fun haveLength(length: Int): Matcher<String> = object : Matcher<String> {
  override fun test(value: String) = Result(value.length == length,
      "${convertValueToString(value)} should have length $length",
      "${convertValueToString(value)} should not have length $length")
}
