package io.kotest.matchers.string

import io.kotest.assertions.print.print
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
   val ok = value.startsWith(prefix)
   var msg = "${value.print().value} should start with ${prefix.print().value}"
   val notmsg = "${value.print().value} should not start with ${prefix.print().value}"
   if (!ok) {
      for (k in 0 until min(value.length, prefix.length)) {
         if (value[k] != prefix[k]) {
            msg = "$msg (diverged at index $k)"
            break
         }
      }
   }
   MatcherResult(
      ok,
      { msg },
      { notmsg })
}

infix fun <A : CharSequence?> A.shouldStartWith(regex: Regex): A {
   this should startWith(regex)
   return this
}

fun startWith(regex: Regex): Matcher<CharSequence?> = neverNullMatcher { value ->
   val ok = regex.matchesAt(value, 0)
   MatcherResult(
      ok,
      { "${value.print().value} should start with regex ${regex.pattern}" },
      { "${value.print().value} should not start with regex ${regex.pattern}" }
   )
}
