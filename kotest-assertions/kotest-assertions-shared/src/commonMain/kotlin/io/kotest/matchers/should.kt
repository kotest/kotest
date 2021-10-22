package io.kotest.matchers

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.assertionCounter
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.eq.actualIsNull
import io.kotest.assertions.eq.eq
import io.kotest.assertions.eq.expectedIsNull
import io.kotest.assertions.errorCollector
import io.kotest.assertions.failure
import io.kotest.assertions.intellijFormatError
import io.kotest.assertions.show.Printed
import io.kotest.assertions.show.show

@Suppress("UNCHECKED_CAST")
infix fun <T, U : T> T.shouldBe(expected: U?) {
   when (expected) {
      is Matcher<*> -> should(expected as Matcher<T>)
      else -> {
         val actual = this
         assertionCounter.inc()
         eq(actual, expected)?.let(errorCollector::collectOrThrow)
      }
   }
}

@Suppress("UNCHECKED_CAST")
infix fun <T> T.shouldNotBe(any: Any?) {
   when (any) {
      is Matcher<*> -> shouldNot(any as Matcher<T>)
      else -> shouldNot(equalityMatcher(any))
   }
}

infix fun <T> T.shouldHave(matcher: Matcher<T>) = should(matcher)
infix fun <T> T.should(matcher: Matcher<T>) {
   invokeMatcher(this, matcher)
}

fun <T> invokeMatcher(t: T, matcher: Matcher<T>): T {
   assertionCounter.inc()
   val result = matcher.test(t)
   if (!result.passed()) {
      when (result) {
         is ComparableMatcherResult -> errorCollector.collectOrThrow(
            failure(
               Expected(Printed(result.expected())),
               Actual(Printed(result.actual())),
               result.failureMessage()
            )
         )
         else -> errorCollector.collectOrThrow(failure(result.failureMessage()))
      }
   }
   return t
}

infix fun <T> T.shouldNotHave(matcher: Matcher<T>) = shouldNot(matcher)
infix fun <T> T.shouldNot(matcher: Matcher<T>) = should(matcher.invert())

infix fun <T> T.should(matcher: (T) -> Unit) = matcher(this)

fun <T> be(expected: T) = equalityMatcher(expected)
fun <T> equalityMatcher(expected: T) = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val t = if (value == null && expected == null) {
         null
      } else if (value == null && expected != null) {
         actualIsNull(expected)
      } else if (value != null && expected == null) {
         expectedIsNull(value)
      } else {
         eq(value, expected)
      }
      return MatcherResult(
         t == null,
         {
            val e = Expected(expected.show())
            val a = Actual(value.show())
            failure(e, a).message ?: intellijFormatError(e, a)
         },
         { "${expected.show().value} should not equal ${value.show().value}" }
      )
   }
}



