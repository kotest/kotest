package io.kotest.matchers.regex

import io.kotest.*

fun haveSamePatternMatcher(pattern: String) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      return MatcherResult(
         value.pattern == pattern,
         { "Regex should have pattern ${pattern} but has pattern ${value.pattern}" },
         { "Regex should not have pattern ${value.pattern}" }
      )
   }
}

fun haveSameRegexOptionsMatcher(options: Set<RegexOption>) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      return MatcherResult(
         value.options == options,
         { "Regex should have options ${options} but has options ${value.options}" },
         { "Regex should not have pattern ${value.options}" }
      )
   }
}

fun areEqualRegexMatcher(regex: Regex) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      val patternMatchingResult = haveSamePatternMatcher(regex.pattern).test(value)
      val optionMatchingResult = haveSameRegexOptionsMatcher(regex.options).test(value)
      return MatcherResult(
         patternMatchingResult.passed() && optionMatchingResult.passed(),
         { "Regex should have pattern ${regex.pattern} and regex options ${regex.options}, but has pattern ${value.pattern} and regex options ${value.options}." },
         { "Regex should not have pattern ${value.pattern} and regex options ${value.options}." }
      )
   }
}

fun beRegex(regex: Regex) = areEqualRegexMatcher(regex)

fun havePatter(pattern: String) = haveSamePatternMatcher(pattern)

fun haveOptions(options: Set<RegexOption>) = haveSameRegexOptionsMatcher(options)

infix fun Regex.shouldBeRegex(anotherRegex: Regex) = this should beRegex(anotherRegex)

infix fun Regex.shouldNotBeRegex(anotherRegex: Regex) = this shouldNot beRegex(anotherRegex)

infix fun Regex.shouldHavePattern(regexPattern: String) = this should havePatter(regexPattern)

infix fun Regex.shouldNotHavePattern(regexPattern: String) = this shouldNot havePatter(regexPattern)

infix fun Regex.shouldHaveAllRegexOptions(regexOption: Set<RegexOption>) = this should haveOptions(regexOption)

infix fun Regex.shouldNotHaveAllRegexOptions(regexOption: Set<RegexOption>) = this shouldNot haveOptions(regexOption)
