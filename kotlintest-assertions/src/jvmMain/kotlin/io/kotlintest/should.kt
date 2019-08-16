package io.kotlintest

import io.kotlintest.assertions.ErrorCollector
import io.kotlintest.assertions.Failures
import io.kotlintest.assertions.clueContextAsString
import io.kotlintest.assertions.collectOrThrow

@Suppress("UNCHECKED_CAST")
infix fun <T, U : T> T.shouldBe(any: U?) {
  when (any) {
    is Matcher<*> -> should(any as Matcher<T>)
    else -> {
      if (this == null && any != null) {
        ErrorCollector.collectOrThrow(equalsError(any, this))
      } else if (!compare(this, any)) {
        ErrorCollector.collectOrThrow(equalsError(any, this))
      }
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

// -- matcher functions

infix fun <T> T.shouldHave(matcher: Matcher<T>) = should(matcher)
infix fun <T> T.should(matcher: Matcher<T>) {
  val result = matcher.test(this)
  if (!result.passed()) {
    ErrorCollector.collectOrThrow(Failures.failure(clueContextAsString() + result.failureMessage()))
  }
}

infix fun <T> T.shouldNotHave(matcher: Matcher<T>) = shouldNot(matcher)
infix fun <T> T.shouldNot(matcher: Matcher<T>) = should(matcher.invert())

infix fun <T> T.should(matcher: (T) -> Unit) = matcher(this)
