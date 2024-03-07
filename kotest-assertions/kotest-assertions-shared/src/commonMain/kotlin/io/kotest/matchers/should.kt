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
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.print

@Suppress("UNCHECKED_CAST")
infix fun <T, U : T> T.shouldBe(expected: U?): T {
   when (expected) {
      is Matcher<*> -> should(expected as Matcher<T>)
      else -> {
         val actual = this
         assertionCounter.inc()
         eq(actual, expected)?.let(errorCollector::collectOrThrow)
      }
   }
   return this
}

@Suppress("UNCHECKED_CAST")
infix fun <T> T.shouldNotBe(any: Any?): T {
   when (any) {
      is Matcher<*> -> shouldNot(any as Matcher<T>)
      else -> shouldNot(equalityMatcher(any))
   }
   return this
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
               expected = Expected(Printed(result.expected())),
               actual = Actual(Printed(result.actual())),
               prependMessage = result.failureMessage() + "\n"
            )
         )
         is EqualityMatcherResult -> errorCollector.collectOrThrow(
            failure(
               expected = Expected(result.expected().print()),
               actual = Actual(result.actual().print()),
               prependMessage = result.failureMessage() + "\n"
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
            val e = Expected(expected.print())
            val a = Actual(value.print())
            failure(e, a).message ?: intellijFormatError(e, a)
         },
         { "${expected.print().value} should not equal ${value.print().value}" }
      )
   }
}
