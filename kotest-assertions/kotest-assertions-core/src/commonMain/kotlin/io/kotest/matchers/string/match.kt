package io.kotest.matchers.string

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <A : CharSequence> A?.shouldMatch(regex: String): A {
   this should match(regex)
   return this!!
}

infix fun <A : CharSequence> A?.shouldMatch(regex: Regex): A {
   this should match(regex)
   return this!!
}

infix fun <A : CharSequence> A?.shouldNotMatch(regex: String): A {
   this shouldNot match(regex)
   return this!!
}

infix fun <A : CharSequence> A?.shouldNotMatch(regex: Regex): A {
   this shouldNot match(regex)
   return this!!
}

fun match(regex: Regex): Matcher<CharSequence?> = neverNullMatcher { value ->
   MatcherResult(
      value.matches(regex),
      { "${value.print().value} should match regex $regex" },
      {
         "${value.print().value} should not match regex $regex"
      })
}

fun match(regex: CharSequence): Matcher<CharSequence?> = match(regex.toString().toRegex())
