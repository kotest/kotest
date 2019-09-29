package io.kotest

import io.kotest.assertions.AssertionCounter
import io.kotest.assertions.ErrorCollector
import io.kotest.assertions.Failures
import io.kotest.assertions.clueContextAsString
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.compare
import io.kotest.assertions.diffLargeString
import io.kotest.assertions.readSystemProperty
import io.kotest.assertions.stringRepr

@Suppress("UNCHECKED_CAST")
infix fun <T, U : T> T.shouldBe(any: U?) {
   when (any) {
      is Matcher<*> -> should(any as Matcher<T>)
      else -> {
         AssertionCounter.inc()
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

infix fun <T> T.shouldHave(matcher: Matcher<T>) = should(matcher)
infix fun <T> T.should(matcher: Matcher<T>) {
   AssertionCounter.inc()
   val result = matcher.test(this)
   if (!result.passed()) {
      ErrorCollector.collectOrThrow(Failures.failure(clueContextAsString() + result.failureMessage()))
   }
}

infix fun <T> T.shouldNotHave(matcher: Matcher<T>) = shouldNot(matcher)
infix fun <T> T.shouldNot(matcher: Matcher<T>) = should(matcher.invert())

infix fun <T> T.should(matcher: (T) -> Unit) = matcher(this)

fun <T> be(expected: T) = equalityMatcher(expected)
fun <T> equalityMatcher(expected: T) = object : Matcher<T> {
  override fun test(value: T): MatcherResult {
    val expectedRepr = stringRepr(expected)
    val valueRepr = stringRepr(value)
    return MatcherResult(
      compare(expected, value),
      { equalsErrorMessage(expectedRepr, valueRepr) },
      { "$expectedRepr should not equal $valueRepr" }
    )
  }
}

internal fun equalsError(expected: Any?, actual: Any?): Throwable {
  val largeStringDiffMinSize = readSystemProperty("kotest.assertions.multi-line-diff-size", "50").toInt()
  val (expectedRepr, actualRepr) = diffLargeString(stringRepr(expected), stringRepr(actual), largeStringDiffMinSize)
  val message = clueContextAsString() + equalsErrorMessage(expectedRepr, actualRepr)
  return Failures.failure(message, expectedRepr, actualRepr)
}

internal fun equalsErrorMessage(expected: Any?, actual: Any?) = "expected: $expected but was: $actual"

