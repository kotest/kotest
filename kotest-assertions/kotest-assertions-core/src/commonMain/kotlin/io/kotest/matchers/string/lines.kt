package io.kotest.matchers.string

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <A : CharSequence> A?.shouldBeSingleLine(): A {
   this should haveLineCount(1)
   return this!!
}

fun <A : CharSequence> A?.shouldNotBeSingleLine(): A {
   this shouldNot haveLineCount(1)
   return this!!
}

infix fun <A : CharSequence> A?.shouldHaveLineCount(count: Int): A {
   this should haveLineCount(count)
   return this!!
}

infix fun <A : CharSequence> A?.shouldNotHaveLineCount(count: Int): A {
   this shouldNot haveLineCount(count)
   return this!!
}

/**
 * Match on the number of newlines in a string.
 *
 * This will count both "\n" and "\r\n", and so is not dependant on the system line separator.
 */
fun haveLineCount(count: Int): Matcher<CharSequence?> = neverNullMatcher<CharSequence> { value ->
   // plus one because we always have one more line than the new line character
   val lines = if (value.isEmpty()) 0 else value.count { it == '\n' } + 1
   MatcherResult(
      lines == count,
      { "${value.print().value} should have $count lines but had $lines" },
      { "${value.print().value} should not have $count lines" }
   )
}
