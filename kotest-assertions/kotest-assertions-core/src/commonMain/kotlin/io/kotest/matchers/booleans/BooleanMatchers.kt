package io.kotest.matchers.booleans

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Asserts that this [Boolean] is true
 *
 * Verifies that this (nullable) [Boolean] or boolean expression is true.
 * Opposite of [Boolean.shouldNotBeTrue]
 *
 * ```
 * true.shouldBeTrue    // Assertion passes
 * false.shouldBeTrue   // Assertion fails
 * null.shouldBeTrue    // Assertion fails
 *
 * (3 + 3 == 6).shouldBeTrue    // Assertion passes
 * (3 + 3 == 42).shouldBeTrue   // Assertion fails
 * ```
 * @see [Boolean?.shouldNotBeFalse]
 * @see [Boolean?.shouldBeFalse]
 */
@OptIn(ExperimentalContracts::class)
fun Boolean?.shouldBeTrue(): Boolean {
   contract {
      returns() implies (this@shouldBeTrue != null)
   }

   this should beTrue()
   return this!!
}
/**
 * Asserts that this [Boolean] is not true
 *
 * Verifies that this (nullable) [Boolean] or boolean expression is not true.
 * Opposite of [Boolean.shouldBeTrue]
 *
 * ```
 * false.shouldNotBeTrue   // Assertion passes
 * null.shouldNotBeTrue    // Assertion passes
 * true.shouldNotBeTrue    // Assertion fails
 *
 * (3 + 3 == 42).shouldNotBeTrue   // Assertion passes
 * (3 + 3 == 6).shouldNotBeTrue    // Assertion fails
 * ```
 * @see [Boolean?.shouldBeFalse]
 * @see [Boolean?.shouldNotBeFalse]
 */
fun Boolean?.shouldNotBeTrue(): Boolean? {
   this shouldNot beTrue()
   return this
}

/**
 * Asserts that this [Boolean] is false
 *
 * Verifies that this (nullable) [Boolean] or boolean expression is false.
 * Opposite of [Boolean.shouldNotBeFalse]
 *
 * ```
 * false.shouldBeFalse  // Assertion passes
 * true.shouldBeFalse   // Assertion fails
 * null.shouldBeFalse  // Assertion fails
 *
 * (3 + 3 == 42).shouldBeFalse  // Assertion passes
 * (3 + 3 == 6).shouldBeFalse   // Assertion fails
 * ```
 * @see [Boolean?.shouldNotBeTrue]
 * @see [Boolean?.shouldBeTrue]
 */
@OptIn(ExperimentalContracts::class)
fun Boolean?.shouldBeFalse(): Boolean {
   contract {
      returns() implies (this@shouldBeFalse != null)
   }

   this should beFalse()
   return this!!
}

/**
 * Asserts that this [Boolean] is not false
 *
 * Verifies that this (nullable) [Boolean] or boolean expression is not false.
 * Opposite of [Boolean.shouldBeFalse]
 *
 * ```
 * true.shouldNotBeFalse   // Assertion passes
 * null.shouldNotBeFalse   // Assertion passes
 * false.shouldNotBeFalse  // Assertion fails
 *
 * (3 + 3 == 6).shouldNotBeFalse   // Assertion passes
 * (3 + 3 == 42).shouldNotBeFalse  // Assertion fails
 * ```
 * @see [Boolean?.shouldBeTrue]
 * @see [Boolean?.shouldNotBeTrue]
 */
fun Boolean?.shouldNotBeFalse(): Boolean? {
   this shouldNot beFalse()
   return this
}

/**
 * Match that verifies a given [Boolean] is `true`.
 */
fun beTrue() = beBoolean(true)

/**
 * Match that verifies a given [Boolean] is `false`.
 */
fun beFalse() = beBoolean(false)

fun beBoolean(expected: Boolean) = object : Matcher<Boolean?> {
   override fun test(value: Boolean?) = MatcherResult(
      value == expected,
      { "$value should equal $expected" },
      { "$value should not equal $expected" }
   )
}
