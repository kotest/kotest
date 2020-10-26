package io.kotest.matchers.string

import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun String?.shouldHaveMaxLength(length: Int): String? {
   this should haveMaxLength(length)
   return this
}

infix fun String?.shouldNotHaveMaxLength(length: Int): String? {
   this shouldNot haveMaxLength(length)
   return this
}

fun haveMaxLength(length: Int) = neverNullMatcher<String> { value ->
   MatcherResult(
      value.length <= length,
      "${value.show().value} should have maximum length of $length",
      "${value.show().value} should have minimum length of ${length - 1}"
   )
}

infix fun String?.shouldHaveMinLength(length: Int): String? {
   this should haveMinLength(length)
   return this
}

infix fun String?.shouldNotHaveMinLength(length: Int): String? {
   this shouldNot haveMinLength(length)
   return this
}

fun haveMinLength(length: Int) = neverNullMatcher<String> { value ->
   MatcherResult(
      value.length >= length,
      "${value.show().value} should have minimum length of $length",
      "${value.show().value} should have maximum length of ${length - 1}"
   )
}


fun String?.shouldHaveLengthBetween(min: Int, max: Int): String? {
   this should haveLengthBetween(min, max)
   return this
}

fun String?.shouldNotHaveLengthBetween(min: Int, max: Int): String? {
   this shouldNot haveLengthBetween(min, max)
   return this
}

fun haveLengthBetween(min: Int, max: Int): Matcher<String?> {
   require(min <= max)
   return neverNullMatcher { value ->
      MatcherResult(
         value.length in min..max,
         "${value.show().value} should have length in $min..$max but was ${value.length}",
         "${value.show().value} should not have length between $min and $max"
      )
   }
}


fun String?.shouldHaveLengthIn(range: IntRange): String? {
   this should haveLengthIn(range)
   return this
}

fun String?.shouldNotHaveLengthIn(range: IntRange): String? {
   this shouldNot haveLengthIn(range)
   return this
}

fun haveLengthIn(range: IntRange): Matcher<String?> {
   return neverNullMatcher { value ->
      MatcherResult(
         value.length in range,
         "${value.show().value} should have length in $range but was ${value.length}",
         "${value.show().value} should not have length between $range"
      )
   }
}


infix fun String?.shouldHaveLength(length: Int): String? {
   this should haveLength(length)
   return this
}

infix fun String?.shouldNotHaveLength(length: Int): String? {
   this shouldNot haveLength(length)
   return this
}


infix fun String?.shouldHaveSameLengthAs(other: String): String? {
   this should haveSameLengthAs(other)
   return this
}

infix fun String?.shouldNotHaveSameLengthAs(other: String): String? {
   this shouldNot haveSameLengthAs(other)
   return this
}

fun haveSameLengthAs(other: String) = neverNullMatcher<String> { value ->
   MatcherResult(
      value.length == other.length,
      "${value.show().value} should have the same length as ${other.show().value}",
      "${value.show().value} should not have the same length as ${other.show().value}"
   )
}
