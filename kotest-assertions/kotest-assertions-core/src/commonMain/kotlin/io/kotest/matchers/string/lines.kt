package io.kotest.matchers.string

import io.kotest.assertions.show.show
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun String?.shouldBeSingleLine(): String? {
   this should haveLineCount(1)
   return this
}

fun String?.shouldNotBeSingleLine(): String? {
   this shouldNot haveLineCount(1)
   return this
}

infix fun String?.shouldHaveLineCount(count: Int): String? {
   this should haveLineCount(count)
   return this
}

infix fun String?.shouldNotHaveLineCount(count: Int): String? {
   this shouldNot haveLineCount(count)
   return this
}

/**
 * Match on the number of newlines in a string.
 *
 * This will count both "\n" and "\r\n", and so is not dependant on the system line separator.
 */
fun haveLineCount(count: Int) = neverNullMatcher<String> { value ->
   // plus one because we always have one more line than the new line character
   val lines = if (value.isEmpty()) 0 else value.count { it == '\n' } + 1
   MatcherResult(
      lines == count,
      { "${value.show().value} should have $count lines but had $lines" },
      { "${value.show().value} should not have $count lines" }
   )
}
