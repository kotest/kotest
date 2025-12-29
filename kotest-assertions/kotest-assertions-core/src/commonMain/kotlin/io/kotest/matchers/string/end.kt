package io.kotest.matchers.string

import io.kotest.assertions.print.print
import io.kotest.assertions.submatching.describePartialMatchesInStringForSuffix
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <A : CharSequence> A?.shouldEndWith(suffix: CharSequence): A {
   this should endWith(suffix)
   return this!!
}

infix fun <A : CharSequence> A?.shouldNotEndWith(suffix: CharSequence): A {
   this shouldNot endWith(suffix)
   return this!!
}

fun endWith(suffix: CharSequence): Matcher<CharSequence?> = neverNullMatcher { value ->

   val escapedValue = StringPreprocessor.process(value)
   val escapedSuffix = StringPreprocessor.process(suffix)

   val passed = escapedValue.endsWith(escapedSuffix)
   val shortMessage = "${escapedValue.print().value} should end with ${escapedSuffix.print().value}"
   val possibleSubmatches =
      if (passed) "" else describePartialMatchesInStringForSuffix(escapedSuffix.toString(), escapedValue.toString()).toString()
   val message = listOf(shortMessage, possibleSubmatches).filter { it.isNotEmpty() }.joinToString("\n")
   MatcherResult(
      passed,
      { message },
      { "${escapedValue.print().value} should not end with ${escapedSuffix.print().value}" }
   )
}

infix fun <A : CharSequence?> A.shouldEndWith(regex: Regex): A {
   this should endWith(regex)
   return this
}

fun endWith(regex: Regex): Matcher<CharSequence?> = neverNullMatcher { value ->
   val endWithRegex = ".*${regex.pattern}$".toRegex()
   MatcherResult(
      value matches endWithRegex,
      { "${value.print().value} should end with regex ${regex.pattern}" },
      { "${value.print().value} should not end with regex ${regex.pattern}" }
   )
}
