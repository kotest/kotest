package io.kotest.matchers.string

import io.kotest.assertions.show.show
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <A : CharSequence> A?.shouldContain(substr: String): A? {
   this.toString() should contain(substr)
   return this
}

infix fun <A : CharSequence> A?.shouldNotContain(substr: String): A? {
   this.toString() shouldNot contain(substr)
   return this
}

fun contain(substr: String) = include(substr)

infix fun <A : CharSequence> A?.shouldInclude(substr: String): A? {
   this.toString() should include(substr)
   return this
}

infix fun <A : CharSequence> A?.shouldNotInclude(substr: String): A? {
   this.toString() shouldNot include(substr)
   return this
}

infix fun <A : CharSequence> A?.shouldContainIgnoringCase(substr: String): A? {
   this.toString() should containIgnoringCase(substr)
   return this
}

infix fun <A : CharSequence> A?.shouldNotContainIgnoringCase(substr: String): A? {
   this.toString() shouldNot containIgnoringCase(substr)
   return this
}

fun containIgnoringCase(substr: String) = neverNullMatcher<String> { value ->
   MatcherResult(
      value.toLowerCase().indexOf(substr.toLowerCase()) >= 0,
      { "${value.show().value} should contain the substring ${substr.show().value} (case insensitive)" },
      { "${value.show().value} should not contain the substring ${substr.show().value} (case insensitive)" }
   )
}

infix fun <A : CharSequence> A?.shouldContain(regex: Regex): A? {
   this.toString() should contain(regex)
   return this
}

infix fun <A : CharSequence> A?.shouldNotContain(regex: Regex): A? {
   this.toString() shouldNot contain(regex)
   return this
}

fun contain(regex: Regex) = neverNullMatcher<String> { value ->
   MatcherResult(
      value.contains(regex),
      { "${value.show().value} should contain regex $regex" },
      { "${value.show().value} should not contain regex $regex" })
}

fun <A : CharSequence> A?.shouldContainInOrder(vararg substrings: String): A? {
   this.toString() should containInOrder(*substrings)
   return this
}

fun <A : CharSequence> A?.shouldNotContainInOrder(vararg substrings: String): A? {
   this.toString() shouldNot containInOrder(*substrings)
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

fun include(substr: String) = neverNullMatcher<String> { value ->
   MatcherResult(
      value.contains(substr),
      "${value.show().value} should include substring ${substr.show().value}",
      "${value.show().value} should not include substring ${substr.show().value}"
   )
}

infix fun <A : CharSequence> A?.shouldContainOnlyOnce(substr: String): A? {
   this.toString() should containOnlyOnce(substr)
   return this
}

infix fun <A : CharSequence> A?.shouldNotContainOnlyOnce(substr: String): A? {
   this.toString() shouldNot containOnlyOnce(substr)
   return this
}

fun containOnlyOnce(substring: String) = neverNullMatcher<String> { value ->
   MatcherResult(
      value.indexOf(substring) >= 0 && value.indexOf(substring) == value.lastIndexOf(substring),
      "${value.show().value} should contain the substring ${substring.show().value} exactly once",
      "${value.show().value} should not contain the substring ${substring.show().value} exactly once"
   )
}
