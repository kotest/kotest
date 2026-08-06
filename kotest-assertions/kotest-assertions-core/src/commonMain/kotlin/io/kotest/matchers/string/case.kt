package io.kotest.matchers.string

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

@IgnorableReturnValue
fun <A : CharSequence> A?.shouldBeUpperCase(): A {
   this should beUpperCase()
   return this!!
}

@IgnorableReturnValue
fun <A : CharSequence> A?.shouldNotBeUpperCase(): A {
   this shouldNot beUpperCase()
   return this!!
}

fun beUpperCase(): Matcher<CharSequence?> = neverNullMatcher { value ->
   MatcherResult(
      value.toString() == value.toString().uppercase(),
      { "${value.print().value} should be upper case" },
      { "${value.print().value} should not be upper case" }
   )
}

@IgnorableReturnValue
fun <A : CharSequence?> A.shouldBeLowerCase(): A {
   this should beLowerCase()
   return this
}

@IgnorableReturnValue
fun <A : CharSequence?> A.shouldNotBeLowerCase(): A {
   this shouldNot beLowerCase()
   return this
}

fun beLowerCase(): Matcher<CharSequence?> = neverNullMatcher { value ->
   MatcherResult(
      value.toString() == value.toString().lowercase(),
      { "${value.print().value} should be lower case" },
      { "${value.print().value} should not be lower case" }
   )
}
