package io.kotest.matchers.regex

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot

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

fun haveRegexOptionMatcher(option: RegexOption) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      return MatcherResult(
         value.options.contains(option),
         { "Regex options should contains $option" },
         { "Regex options should not contains $option" }
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

fun haveOption(option: RegexOption) = haveRegexOptionMatcher(option)

infix fun Regex.shouldBeRegex(anotherRegex: Regex) = this should beRegex(anotherRegex)

infix fun Regex.shouldNotBeRegex(anotherRegex: Regex) = this shouldNot beRegex(anotherRegex)

infix fun Regex.shouldHavePattern(regexPattern: String) = this should havePatter(regexPattern)

infix fun Regex.shouldNotHavePattern(regexPattern: String) = this shouldNot havePatter(regexPattern)

infix fun Regex.shouldHaveAllRegexOptions(regexOptions: Set<RegexOption>) = this should haveOptions(regexOptions)

infix fun Regex.shouldNotHaveAllRegexOptions(regexOptions: Set<RegexOption>) = this shouldNot haveOptions(regexOptions)

infix fun Regex.shouldHaveRegexOption(regexOption: RegexOption) = this should haveOption(regexOption)

infix fun Regex.shouldNotHaveRegexOption(regexOption: RegexOption) = this shouldNot haveOption(regexOption)
