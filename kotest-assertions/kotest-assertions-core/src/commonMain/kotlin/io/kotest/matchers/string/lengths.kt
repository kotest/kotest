package io.kotest.matchers.string

import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <A : CharSequence> A?.shouldHaveMaxLength(length: Int): A {
   this should haveMaxLength(length)
   return this!!
}

infix fun <A : CharSequence> A?.shouldNotHaveMaxLength(length: Int): A {
   this shouldNot haveMaxLength(length)
   return this!!
}

fun haveMaxLength(length: Int): Matcher<CharSequence?> = neverNullMatcher { value ->
   MatcherResult(
      value.length <= length,
      { "${value.show().value} should have maximum length of $length" },
      {
         "${value.show().value} should have minimum length of ${length - 1}"
      })
}

infix fun <A : CharSequence> A?.shouldHaveMinLength(length: Int): A {
   this should haveMinLength(length)
   return this!!
}

infix fun <A : CharSequence> A?.shouldNotHaveMinLength(length: Int): A {
   this shouldNot haveMinLength(length)
   return this!!
}

fun haveMinLength(length: Int): Matcher<CharSequence?> = neverNullMatcher { value ->
   MatcherResult(
      value.length >= length,
      { "${value.show().value} should have minimum length of $length" },
      {
         "${value.show().value} should have maximum length of ${length - 1}"
      })
}

fun <A : CharSequence> A?.shouldHaveLengthBetween(min: Int, max: Int): A {
   this should haveLengthBetween(min, max)
   return this!!
}

fun <A : CharSequence> A?.shouldNotHaveLengthBetween(min: Int, max: Int): A {
   this shouldNot haveLengthBetween(min, max)
   return this!!
}

fun haveLengthBetween(min: Int, max: Int): Matcher<CharSequence?> {
   require(min <= max)
   return neverNullMatcher { value ->
      MatcherResult(
         value.length in min..max,
         { "${value.show().value} should have length in $min..$max but was ${value.length}" },
         {
            "${value.show().value} should not have length between $min and $max"
         })
   }
}


fun <A : CharSequence> A?.shouldHaveLengthIn(range: IntRange): A {
   this should haveLengthIn(range)
   return this!!
}

fun <A : CharSequence> A?.shouldNotHaveLengthIn(range: IntRange): A {
   this shouldNot haveLengthIn(range)
   return this!!
}

fun haveLengthIn(range: IntRange): Matcher<CharSequence?> {
   return neverNullMatcher { value ->
      MatcherResult(
         value.length in range,
         { "${value.show().value} should have length in $range but was ${value.length}" },
         {
            "${value.show().value} should not have length between $range"
         })
   }
}


infix fun <A : CharSequence> A?.shouldHaveLength(length: Int): A {
   this should haveLength(length)
   return this!!
}

infix fun <A : CharSequence> A?.shouldNotHaveLength(length: Int): A {
   this shouldNot haveLength(length)
   return this!!
}


infix fun <A : CharSequence> A?.shouldHaveSameLengthAs(other: String): A {
   this should haveSameLengthAs(other)
   return this!!
}

infix fun <A : CharSequence> A?.shouldNotHaveSameLengthAs(other: String): A {
   this shouldNot haveSameLengthAs(other)
   return this!!
}

fun haveSameLengthAs(other: CharSequence?): Matcher<CharSequence?> = neverNullMatcher { value ->
   MatcherResult(
      value.length == other?.length,
      { "${value.show().value} should have the same length as ${other.show().value}" },
      {
         "${value.show().value} should not have the same length as ${other.show().value}"
      })
}

fun strlen(length: Int): Matcher<String?> = haveLength(length)

fun haveLength(length: Int): Matcher<CharSequence?> = neverNullMatcher { value ->
   MatcherResult(
      value.length == length,
      { "${value.show().value} should have length $length, but instead was ${value.length}" },
      {
         "${value.show().value} should not have length $length"
      })
}
