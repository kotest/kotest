package io.kotlintest.matchers.boolean

import io.kotlintest.shouldBe

/**
 * Asserts that this [Boolean] is true
 *
 * Verifies that this [Boolean] or boolean expression is true.
 * Opposite of [Boolean.shouldBeFalse]
 *
 * ```
 * true.shouldBeTrue    // Assertion passes
 * false.shouldBeTrue   // Assertion fails
 *
 * (3 + 3 == 6).shouldBeTrue    // Assertion passes
 * (3 + 3 == 42).shouldBeTrue   // Assertion fails
 * ```
 */
fun Boolean.shouldBeTrue() = this shouldBe true


/**
 * Asserts that this [Boolean] is false
 *
 * Verifies that this [Boolean] or boolean expression is false.
 * Opposite of [Boolean.shouldBeTrue]
 *
 * ```
 * false.shouldBeFalse  // Assertion passes
 * true.shouldBeFalse   // Assertion fails
 *
 * (3 + 3 == 42).shouldBeFalse  // Assertion passes
 * (3 + 3 == 6).shouldBeFalse   // Assertion fails
 * ```
 */
fun Boolean.shouldBeFalse() = this shouldBe false
