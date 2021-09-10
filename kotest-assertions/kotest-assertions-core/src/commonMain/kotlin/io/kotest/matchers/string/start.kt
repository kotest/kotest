package io.kotest.matchers.string

import io.kotest.assertions.show.show
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
   var msg = "${value.show().value} should start with ${prefix.show().value}"
   val notmsg = "${value.show().value} should not start with ${prefix.show().value}"
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
