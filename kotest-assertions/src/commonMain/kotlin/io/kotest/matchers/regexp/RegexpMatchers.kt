package io.kotest.matchers.regexp

import io.kotest.*

fun haveSamePatternMatcher(pattern: String) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      return MatcherResult(
         value.pattern == pattern,
         { "Regexp should have pattern ${pattern} but has pattern ${value.pattern}" },
         { "Regexp should not have pattern ${value.pattern}" }
      )
   }
}

fun haveSameRegexOptionsMatcher(options: Set<RegexOption>) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      return MatcherResult(
         value.options == options,
         { "Regexp should have options ${options} but has options ${value.options}" },
         { "Regexp should not have pattern ${value.options}" }
      )
   }
}

fun areEqualRegexpMatcher(regex: Regex) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      val patternMatchingResult = haveSamePatternMatcher(regex.pattern).test(value)
      val optionMatchingResult = haveSameRegexOptionsMatcher(regex.options).test(value)
      return MatcherResult(
         patternMatchingResult.passed() && optionMatchingResult.passed(),
         { "Regexp $regex should be $value" },
         { "Regexp $regex should not be $value" }
      )
   }
}

infix fun Regex.shouldBeRegex(anotherRegex: Regex) = this should areEqualRegexpMatcher(anotherRegex)

infix fun Regex.shouldNotBeRegex(anotherRegex: Regex) = this shouldNotBe areEqualRegexpMatcher(anotherRegex)
