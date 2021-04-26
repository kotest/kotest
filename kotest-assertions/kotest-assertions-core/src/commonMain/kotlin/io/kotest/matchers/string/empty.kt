package io.kotest.matchers.string

import io.kotest.assertions.show.show
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <A : CharSequence> A.shouldBeEmpty(): A {
   this.toString() should beEmpty()
   return this
}

fun <A : CharSequence> A.shouldNotBeEmpty(): A {
   this.toString() shouldNot beEmpty()
   return this
}

fun beEmpty() = neverNullMatcher<String> { value ->
   MatcherResult(
      value.isEmpty(),
      "${value.show().value} should be empty",
      "${value.show().value} should not be empty"
   )
}

fun <A : CharSequence> A.shouldBeBlank(): A {
   this.toString() should beBlank()
   return this
}

fun <A : CharSequence> A.shouldNotBeBlank(): A {
   this.toString() shouldNot beBlank()
   return this
}

fun containOnlyWhitespace() = beBlank()
fun beBlank() = neverNullMatcher<String> { value ->
   MatcherResult(
      value.isBlank(),
      { "${value.show().value} should contain only whitespace" },
      { "${value.show().value} should not contain only whitespace" }
   )
}
