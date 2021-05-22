package io.kotest.matchers.string

import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher

fun haveSubstring(substr: String) = include(substr)

fun match(regex: Regex) = neverNullMatcher<String> { value ->
  MatcherResult(
      value.matches(regex),
      "${value.show().value} should match regex $regex",
      "${value.show().value} should not match regex $regex")
}

fun match(regex: String): Matcher<String?> = match(regex.toRegex())
