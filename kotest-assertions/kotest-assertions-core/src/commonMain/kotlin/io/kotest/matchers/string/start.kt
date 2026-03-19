package io.kotest.matchers.string

import io.kotest.assertions.print.print
import io.kotest.assertions.submatching.describePartialMatchesInStringForPrefix
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.math.min

infix fun <A : CharSequence?> A.shouldStartWith(prefix: CharSequence): A {
   this should startWith(prefix)
   return this
}

infix fun <A : CharSequence?> A.shouldNotStartWith(prefix: CharSequence): A {
   this shouldNot startWith(prefix)
   return this
}

fun startWith(prefix: CharSequence): Matcher<CharSequence?> = neverNullMatcher { value ->

   val escapedValue = StringPreprocessor.process(value)
   val escapedPrefix = StringPreprocessor.process(prefix)

   val ok = escapedValue.startsWith(escapedPrefix)
   var msg = "${escapedValue.print().value} should start with ${escapedPrefix.print().value}"
   val notmsg = "${escapedValue.print().value} should not start with ${escapedPrefix.print().value}"
   if (!ok) {
      for (k in 0 until min(escapedValue.length, escapedPrefix.length)) {
         if (escapedValue[k] != escapedPrefix[k]) {
            msg = "$msg (diverged at index $k)"
            break
         }
      }
      val partialMismatches =
         describePartialMatchesInStringForPrefix(escapedPrefix.toString(), escapedValue.toString()).toString()
      if (partialMismatches.isNotEmpty()) {
         msg = "$msg\n$partialMismatches"
      }
   }
   MatcherResult(
      ok,
      { msg },
      { notmsg }
   )
}

infix fun <A : CharSequence?> A.shouldStartWith(regex: Regex): A {
   this should startWith(regex)
   return this
}

fun startWith(regex: Regex): Matcher<CharSequence?> = neverNullMatcher { value ->
   val escapedValue = StringPreprocessor.process(value)
   val ok = regex.matchesAt(escapedValue, 0)
   MatcherResult(
      ok,
      { "${value.print().value} should start with regex ${regex.pattern}" },
      { "${value.print().value} should not start with regex ${regex.pattern}" }
   )
}
