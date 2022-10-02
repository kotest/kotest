package io.kotest.matchers.regex

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun Regex.shouldMatch(str: String) = this should matchString(str)
infix fun Regex.shouldNotMatch(str: String) = this shouldNot matchString(str)

fun Regex.shouldMatchAll(vararg strs: String) = this should matchAllStrings(*strs)
fun Regex.shouldNotMatchAll(vararg strs: String) = this shouldNot matchAllStrings(*strs)

fun Regex.shouldMatchAny(vararg strs: String) = this should matchAnyStrings(*strs)
fun Regex.shouldNotMatchAny(vararg strs: String) = this shouldNot matchAnyStrings(*strs)

fun matchString(str: String) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      return MatcherResult(
         value.matches(str),
         { "Regex '$value' should match $str" },
         { "Regex '$value' should not match $str" }
      )
   }
}

fun matchAllStrings(vararg strs: String) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      val notMatching = strs.filterNot { value.matches(it) }
      return MatcherResult(
         notMatching.isEmpty(),
         { "Regex '$value' did not match ${notMatching.joinToString(", ")}" },
         { "Regex '$value' should not match ${strs.joinToString(", ")}" }
      )
   }
}

fun matchAnyStrings(vararg strs: String) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      val matched = strs.filter { value.matches(it) }
      return MatcherResult(
         matched.isNotEmpty(),
         { "Regex '$value' did not match any of ${strs.joinToString(", ")}" },
         { "Regex '$value' should not match ${matched.joinToString(", ")}" }
      )
   }
}
