package io.kotest.matchers.equals

import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Verifies that this value is equal to [expected] using [equals].
 *
 * Unlike [io.kotest.matchers.shouldBe], this function restricts the type parameter
 * so that only values of the same type can be compared.
 *
 * ```
 * 1 shouldEq 1       // compiles
 * 1 shouldEq "1"     // compile error — Int vs String
 * ```
 */
infix fun <@kotlin.internal.OnlyInputTypes T> T.shouldEq(expected: T): T {
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
 * 1 shouldNotEq 2       // compiles
 * 1 shouldNotEq "1"     // compile error — Int vs String
 * ```
 */
infix fun <@kotlin.internal.OnlyInputTypes T> T.shouldNotEq(expected: T): T {
   this shouldNot beEqual(expected)
   return this
}
