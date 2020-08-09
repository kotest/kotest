package io.kotest.matchers

import io.kotest.assertions.*
import io.kotest.assertions.eq.actualIsNull
import io.kotest.assertions.eq.eq
import io.kotest.assertions.eq.expectedIsNull
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
   assertionCounter.inc()
   val result = matcher.test(this)
   if (!result.passed()) {
      errorCollector.collectOrThrow(failure(result.failureMessage()))
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



