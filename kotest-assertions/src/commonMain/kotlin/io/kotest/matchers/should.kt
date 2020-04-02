package io.kotest.matchers

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionCounter
import io.kotest.assertions.ErrorCollector
import io.kotest.assertions.Expected
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.eq.eq
import io.kotest.assertions.failure
import io.kotest.assertions.intellijFormatError
import io.kotest.assertions.show.show

@Suppress("UNCHECKED_CAST")
infix fun <T, U : T> T.shouldBe(expected: U?) {
   when (expected) {
      is Matcher<*> -> should(expected as Matcher<T>)
      else -> {
         val actual = this
         AssertionCounter.inc()
         // if we have null and non null, usually that's a failure, but people can override equals to allow it
         if (actual == null && expected != null && actual != expected) {
            ErrorCollector.collectOrThrow(actualIsNull(expected))
         } else if (actual != null && expected == null && actual != expected) {
            ErrorCollector.collectOrThrow(expectedIsNull(actual))
         } else if (actual != null && expected != null) {
            // for two non-null values, we use the [eq] typeclass.
            val t = eq(actual, expected)
            if (t != null)
               ErrorCollector.collectOrThrow(t)
         }
      }
   }
}

private fun actualIsNull(expected: Any): AssertionError {
   return AssertionError("Expected ${expected.show().value} but actual was null")
}

private fun expectedIsNull(actual: Any): AssertionError {
   return AssertionError("Expected null but actual was ${actual.show().value}")
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
   AssertionCounter.inc()
   val result = matcher.test(this)
   if (!result.passed()) {
      ErrorCollector.collectOrThrow(failure(result.failureMessage()))
   }
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



