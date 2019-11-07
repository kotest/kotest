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

fun haveExactOptions(options: Set<RegexOption>) = haveSameRegexOptionsMatcher(options)

fun haveOption(option: RegexOption) = haveRegexOptionMatcher(option)

/**
 * Assert that [Regex] is equal to [anotherRegex] by comparing their pattern and options([RegexOption]).
 * @see [shouldNotBeRegex]
 * @see [beRegex]
 * */
infix fun Regex.shouldBeRegex(anotherRegex: Regex) = this should beRegex(anotherRegex)

/**
 * Assert that [Regex] is not equal to [anotherRegex] by comparing their pattern and options([RegexOption]).
 * @see [shouldBeRegex]
 * @see [beRegex]
 * */
infix fun Regex.shouldNotBeRegex(anotherRegex: Regex) = this shouldNot beRegex(anotherRegex)

/**
 * Assert that [Regex] have pattern [regexPattern].
 * @see [shouldNotHavePattern]
 * @see [havePatter]
 * */
infix fun Regex.shouldHavePattern(regexPattern: String) = this should havePatter(regexPattern)

/**
 * Assert that [Regex] does not have [regexPattern].
 * @see [shouldHavePattern]
 * @see [havePatter]
 * */
infix fun Regex.shouldNotHavePattern(regexPattern: String) = this shouldNot havePatter(regexPattern)

/**
 * Assert that [Regex] have exact regex options as [regexOptions]
 * @see [shouldNotHaveExactRegexOptions]
 * @see [haveExactOptions]
 * */
infix fun Regex.shouldHaveExactRegexOptions(regexOptions: Set<RegexOption>) = this should haveExactOptions(regexOptions)

/**
 * Assert that [Regex] does not have exact regex options as [regexOptions]
 * @see [shouldHaveExactRegexOptions]
 * @see [haveExactOptions]
 * */
infix fun Regex.shouldNotHaveExactRegexOptions(regexOptions: Set<RegexOption>) = this shouldNot haveExactOptions(regexOptions)

/**
 * Assert that [Regex] regex options contains [regexOption]
 * @see [shouldNotHaveRegexOption]
 * @see [haveOption]
 * */
infix fun Regex.shouldHaveRegexOption(regexOption: RegexOption) = this should haveOption(regexOption)

/**
 * Assert that [Regex] regex options does not contains [regexOption]
 * @see [shouldHaveRegexOption]
 * @see [haveOption]
 * */
infix fun Regex.shouldNotHaveRegexOption(regexOption: RegexOption) = this shouldNot haveOption(regexOption)
