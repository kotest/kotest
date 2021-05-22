package io.kotest.matchers.string

import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <A : CharSequence?> A.shouldBeUpperCase(): A {
   this should beUpperCase()
   return this
}

fun String?.shouldNotBeUpperCase(): String? {
   this shouldNot beUpperCase()
   return this
}

fun <A : CharSequence?> beUpperCase(): Matcher<A> = neverNullMatcher { value ->
   MatcherResult(
      value.toString().toUpperCase() == value,
      "${value.show().value} should be upper case",
      "${value.show().value} should not should be upper case"
   )
}

fun <A : CharSequence?> A.shouldBeLowerCase(): A {
   this should beLowerCase()
   return this
}

fun <A : CharSequence?> A.shouldNotBeLowerCase(): A {
   this shouldNot beLowerCase()
   return this
}

fun <A : CharSequence?> beLowerCase() = neverNullMatcher<A> { value ->
   MatcherResult(
      value.toString().toLowerCase() == value,
      "${value.show().value} should be lower case",
      "${value.show().value} should not should be lower case"
   )
}
