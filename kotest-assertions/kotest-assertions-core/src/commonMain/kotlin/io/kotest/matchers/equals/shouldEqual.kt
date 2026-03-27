package io.kotest.matchers.equals

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResultBuilder
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
 * Verifies that this value is equal to [expected] using [equals].
 *
 * Unlike [io.kotest.matchers.shouldBe], this function restricts the type parameter
 * so that only values of the same type can be compared.
 *
 * ```
 * 1 shouldEqual 1       // compiles
 * 1 shouldEqual "1"     // compile error - Int vs String
 * ```
 */
infix fun <@kotlin.internal.OnlyInputTypes T> T.shouldEqual(expected: T): T {
   this should beEqual(expected)
   return this
}

/**
 * Verifies that this value is **not** equal to [expected] using [equals].
 *
 * Unlike [io.kotest.matchers.shouldNotBe], this function restricts the type parameter
 * so that only values of the same type can be compared.
 *
 * ```
 * 1 shouldNotEqual 2       // compiles
 * 1 shouldNotEqual "1"     // compile error - Int vs String
 * ```
 */
infix fun <@kotlin.internal.OnlyInputTypes T> T.shouldNotEqual(expected: T): T {
   this shouldNot beEqual(expected)
   return this
}

/**
 * Verifies that two values are the same using [equals].
 */
fun <A> beEqual(expected: A): Matcher<A> = object : Matcher<A> {
   override fun test(value: A) = MatcherResultBuilder.create(value == expected)
      .withValues(expected = { expected.print() }, actual = { value.print() })
      .withFailureMessage { "$value should be equal to $expected" }
      .withNegatedFailureMessage { "$value should not be equal to $expected" }
      .build()
}
