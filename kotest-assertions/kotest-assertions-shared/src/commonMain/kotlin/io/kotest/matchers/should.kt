package io.kotest.matchers

import io.kotest.assertions.eq.EqMatcher
import io.kotest.assertions.eq.eq
import io.kotest.assertions.withClue

/**
 * Verifies that this value is equal to the expected value.
 *
 * `should` supports two types of arguments on the right hand side.
 *
 * If the argument is a [Matcher] then the matcher is invoked using the left hand side as the input
 * as the matchers expected value.
 *
 * Otherwise, comparison is done using [eq] via an [EqMatcher] to compare left and right.
 *
 * When the power-assert plugin is enabled, this invocation is replaced by shouldBe(expected, msg).
 *
 * @see [eq]
 */
@Suppress("UNCHECKED_CAST")
infix fun <T, U : T> T.shouldBe(expected: U?): T {
   when (expected) {
      is Matcher<*> -> should(expected as Matcher<T>)
      else -> this should be(expected)
   }
   return this
}

/**
 * Verifies that this value is equal to the expected value.
 *
 * `should` supports two types of arguments on the right hand side.
 *
 * If the argument is a [Matcher] then the matcher is invoked using the left hand side as the input
 * as the matchers expected value.
 *
 * Otherwise, comparison is done using [eq] via an [EqMatcher] to compare left and right.
 *
 * This version is used by the power-assert plugin to add extra information to the error message.
 *
 * @see [eq]
 */
@Suppress("UNCHECKED_CAST")
fun <T, U : T> T.shouldBe(expected: U?, msg: String): T {
   when (expected) {
      is Matcher<*> -> should(expected as Matcher<T>)
      else -> withClue(msg) {
         this should be(expected)
      }
   }
   return this
}

@Suppress("UNCHECKED_CAST")
infix fun <T> T.shouldNotBe(any: Any?): T {
   when (any) {
      is Matcher<*> -> shouldNot(any as Matcher<T>)
      else -> this shouldNot be(any)
   }
   return this
}

infix fun <T> T.shouldHave(matcher: Matcher<T>) = should(matcher)
infix fun <T> T.should(matcher: Matcher<T>) {
   invokeMatcher(this, matcher)
}

infix fun <T> T.shouldNotHave(matcher: Matcher<T>) = shouldNot(matcher)
infix fun <T> T.shouldNot(matcher: Matcher<T>) = should(matcher.invert())

infix fun <T> T.should(matcher: (T) -> Unit) = matcher(this)

fun <T> be(expected: T): Matcher<T> = equalityMatcher(expected)
fun <T> equalityMatcher(expected: T): Matcher<T> = EqMatcher(expected)
