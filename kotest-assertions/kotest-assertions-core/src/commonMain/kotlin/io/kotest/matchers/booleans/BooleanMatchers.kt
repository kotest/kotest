package io.kotest.matchers.booleans

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Asserts that this [Boolean] is true
 *
 * Verifies that this [Boolean] or boolean expression is true.
 * Opposite of [Boolean.shouldNotBeTrue]
 *
 * ```
 * true.shouldBeTrue    // Assertion passes
 * false.shouldBeTrue   // Assertion fails
 *
 * (3 + 3 == 6).shouldBeTrue    // Assertion passes
 * (3 + 3 == 42).shouldBeTrue   // Assertion fails
 * ```
 * @see [Boolean.shouldNotBeFalse]
 * @see [Boolean.shouldBeFalse]
 */
fun Boolean.shouldBeTrue() = this shouldBe true

/**
 * Asserts that this [Boolean] is not true
 *
 * Verifies that this [Boolean] or boolean expression is not true.
 * Opposite of [Boolean.shouldBeTrue]
 *
 * ```
 * false.shouldNotBeTrue   // Assertion passes
 * true.shouldNotBeTrue    // Assertion fails
 *
 * (3 + 3 == 42).shouldNotBeTrue   // Assertion passes
 * (3 + 3 == 6).shouldNotBeTrue    // Assertion fails
 * ```
 * @see [Boolean.shouldBeFalse]
 * @see [Boolean.shouldNotBeFalse]
 */
fun Boolean.shouldNotBeTrue() = this shouldNotBe true

/**
 * Asserts that this [Boolean] is false
 *
 * Verifies that this [Boolean] or boolean expression is false.
 * Opposite of [Boolean.shouldNotBeFalse]
 *
 * ```
 * false.shouldBeFalse  // Assertion passes
 * true.shouldBeFalse   // Assertion fails
 *
 * (3 + 3 == 42).shouldBeFalse  // Assertion passes
 * (3 + 3 == 6).shouldBeFalse   // Assertion fails
 * ```
 * @see [Boolean.shouldNotBeTrue]
 * @see [Boolean.shouldBeTrue]
 */
fun Boolean.shouldBeFalse() = this shouldBe false

/**
 * Asserts that this [Boolean] is not false
 *
 * Verifies that this [Boolean] or boolean expression is not false.
 * Opposite of [Boolean.shouldBeFalse]
 *
 * ```
 * true.shouldNotBeFalse   // Assertion passes
 * false.shouldNotBeFalse  // Assertion fails
 *
 * (3 + 3 == 6).shouldNotBeFalse   // Assertion passes
 * (3 + 3 == 42).shouldNotBeFalse  // Assertion fails
 * ```
 * @see [Boolean.shouldBeTrue]
 * @see [Boolean.shouldNotBeTrue]
 */
fun Boolean.shouldNotBeFalse() = this shouldNotBe false
