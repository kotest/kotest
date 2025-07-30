package io.kotest.matchers.regex

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Assert that [Regex] is equal to [anotherRegex] by comparing their pattern and options([RegexOption]).
 *
 * @see [shouldNotBeRegex]
 * @see [beRegex]
 * */
@Deprecated("Use `shouldEqualRegex` instead. Deprecated in 6.0", ReplaceWith("this shouldEqualRegex anotherRegex"))
infix fun Regex.shouldBeRegex(anotherRegex: Regex): Regex = shouldEqualRegex(anotherRegex)

/**
 * Assert that [Regex] is equal to [anotherRegex] by comparing their pattern and options([RegexOption]).
 *
 * @see [shouldNotBeRegex]
 * @see [beRegex]
 * */
infix fun Regex.shouldEqualRegex(anotherRegex: Regex): Regex {
   this should beRegex(anotherRegex)
   return this
}

/**
 * Assert that [Regex] is not equal to [anotherRegex] by comparing their pattern and options([RegexOption]).
 *
 * @see [shouldBeRegex]
 * @see [beRegex]
 * */
@Deprecated("Use `shouldNotEqualRegex` instead. Deprecated in 6.0", ReplaceWith("this shouldNotEqualRegex anotherRegex"))
infix fun Regex.shouldNotBeRegex(anotherRegex: Regex): Regex = shouldEqualRegex(anotherRegex)

/**
 * Assert that [Regex] is not equal to [anotherRegex] by comparing their pattern and options([RegexOption]).
 *
 * @see [shouldBeRegex]
 * @see [beRegex]
 * */
infix fun Regex.shouldNotEqualRegex(anotherRegex: Regex): Regex {
   this shouldNot beRegex(anotherRegex)
   return this
}

fun beRegex(regex: Regex) = areEqualRegexMatcher(regex)

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

/**
 * Assert that [Regex] have pattern [regexPattern].
 * @see [shouldNotHavePattern]
 * @see [havePattern]
 * */
infix fun Regex.shouldHavePattern(regexPattern: String): Regex {
   this should havePattern(regexPattern)
   return this
}

/**
 * Assert that [Regex] does not have [regexPattern].
 * @see [shouldHavePattern]
 * @see [havePattern]
 * */
infix fun Regex.shouldNotHavePattern(regexPattern: String): Regex {
   this shouldNot havePattern(regexPattern)
   return this
}

fun havePattern(pattern: String) = haveSamePatternMatcher(pattern)

fun haveSamePatternMatcher(pattern: String) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      return MatcherResult(
         value.pattern == pattern,
         { "Regex should have pattern $pattern but has pattern ${value.pattern}" },
         { "Regex should not have pattern ${value.pattern}" }
      )
   }
}

/**
 * Assert that [Regex] have exact regex options as [regexOptions]
 * @see [shouldNotHaveExactRegexOptions]
 * @see [haveExactOptions]
 * */
infix fun Regex.shouldHaveExactRegexOptions(regexOptions: Set<RegexOption>): Regex {
   this should haveExactOptions(regexOptions)
   return this
}

/**
 * Assert that [Regex] does not have exact regex options as [regexOptions]
 * @see [shouldHaveExactRegexOptions]
 * @see [haveExactOptions]
 * */
infix fun Regex.shouldNotHaveExactRegexOptions(regexOptions: Set<RegexOption>): Regex {
   this shouldNot haveExactOptions(regexOptions)
   return this
}

fun haveExactOptions(options: Set<RegexOption>) = haveSameRegexOptionsMatcher(options)

fun haveSameRegexOptionsMatcher(options: Set<RegexOption>) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      return MatcherResult(
         value.options == options,
         { "Regex should have options $options but has options ${value.options}" },
         { "Regex should not have pattern ${value.options}" }
      )
   }
}

/**
 * Assert that [Regex] regex options include [regexOption]
 * @see [shouldNotIncludeRegexOption]
 * @see [includeOption]
 * */
infix fun Regex.shouldIncludeRegexOption(regexOption: RegexOption): Regex {
   this should includeOption(regexOption)
   return this
}

/**
 * Assert that [Regex] regex options does not include [regexOption]
 * @see [shouldIncludeRegexOption]
 * @see [includeOption]
 * */
infix fun Regex.shouldNotIncludeRegexOption(regexOption: RegexOption): Regex {
   this shouldNot includeOption(regexOption)
   return this
}

fun includeOption(option: RegexOption) = haveRegexOptionMatcher(option)

fun haveRegexOptionMatcher(option: RegexOption) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      return MatcherResult(
         value.options.contains(option),
         { "Regex options should contains $option" },
         { "Regex options should not contains $option" }
      )
   }
}

/**
 * Assert that [Regex] regex options include [regexOptions]
 * @see [shouldNotIncludeRegexOptions]
 * @see [includeOptions]
 * */
infix fun Regex.shouldIncludeRegexOptions(regexOptions: Set<RegexOption>): Regex {
   this should includeOptions(regexOptions)
   return this
}

/**
 * Assert that [Regex] regex options does not include [regexOptions]
 * @see [shouldIncludeRegexOptions]
 * @see [includeOptions]
 * */
infix fun Regex.shouldNotIncludeRegexOptions(regexOptions: Set<RegexOption>): Regex {
   this shouldNot includeOptions(regexOptions)
   return this
}

fun includeOptions(options: Set<RegexOption>) = haveRegexOptionMatcher(options)

fun haveRegexOptionMatcher(options: Set<RegexOption>) = object : Matcher<Regex> {
   override fun test(value: Regex): MatcherResult {
      return MatcherResult(
         value.options.containsAll(options),
         { "Regex options should contains $options, but missing ${options.filterNot { value.options.contains(it) }}." },
         { "Regex options should not contains $options, but containing ${options.filter { value.options.contains(it) }}." }
      )
   }
}
