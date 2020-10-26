package io.kotest.matchers.string

import io.kotest.assertions.show.show
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun String?.shouldBeUpperCase(): String? {
   this should beUpperCase()
   return this
}

fun String?.shouldNotBeUpperCase(): String? {
   this shouldNot beUpperCase()
   return this
}

fun beUpperCase() = neverNullMatcher<String> { value ->
   MatcherResult(
      value.toUpperCase() == value,
      "${value.show().value} should be upper case",
      "${value.show().value} should not should be upper case"
   )
}

fun String?.shouldBeLowerCase(): String? {
   this should beLowerCase()
   return this
}

fun String?.shouldNotBeLowerCase(): String? {
   this shouldNot beLowerCase()
   return this
}

fun beLowerCase() = neverNullMatcher<String> { value ->
   MatcherResult(
      value.toLowerCase() == value,
      "${value.show().value} should be lower case",
      "${value.show().value} should not should be lower case"
   )
}
