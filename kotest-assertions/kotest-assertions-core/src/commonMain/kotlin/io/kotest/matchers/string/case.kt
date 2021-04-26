package io.kotest.matchers.string

import io.kotest.assertions.show.show
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <A : CharSequence> A?.shouldBeUpperCase(): A? {
   this.toString() should beUpperCase()
   return this
}

fun <A : CharSequence> A?.shouldNotBeUpperCase(): A? {
   this.toString() shouldNot beUpperCase()
   return this
}

fun beUpperCase() = neverNullMatcher<String> { value ->
   MatcherResult(
      value.toUpperCase() == value,
      "${value.show().value} should be upper case",
      "${value.show().value} should not should be upper case"
   )
}

fun <A : CharSequence> A?.shouldBeLowerCase(): A? {
   this.toString() should beLowerCase()
   return this
}

fun <A : CharSequence> A?.shouldNotBeLowerCase(): A? {
   this.toString() shouldNot beLowerCase()
   return this
}

fun beLowerCase() = neverNullMatcher<String> { value ->
   MatcherResult(
      value.toLowerCase() == value,
      "${value.show().value} should be lower case",
      "${value.show().value} should not should be lower case"
   )
}
