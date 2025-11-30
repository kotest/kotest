package io.kotest.matchers.equals

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.ComparisonMatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <A : Any> A.shouldBeEqual(expected: A): A {
   this should beEqual(expected)
   return this
}

infix fun <A : Any> A.shouldNotBeEqual(expected: A): A {
   this shouldNot beEqual(expected)
   return this
}

/**
 * Verifies that two values are the same using [equals].
 */
fun <A> beEqual(expected: A): Matcher<A> = object : Matcher<A> {
   override fun test(value: A) = ComparisonMatcherResult(
      passed = value == expected,
      expected = expected.print(),
      actual = value.print(),
      failureMessageFn = { "$value should be equal to $expected" },
      negatedFailureMessageFn = { "$value should not be equal to $expected" })
}
