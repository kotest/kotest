package io.kotest.matchers.atomic

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Asserts that this [AtomicBoolean] is true
 *
 * Verifies that this [AtomicBoolean] or AtomicBoolean expression is true.
 * Opposite of [AtomicBoolean.shouldNotBeTrue]
 *
 * ```
 * AtomicBoolean(true).shouldBeTrue    // Assertion passes
 * AtomicBoolean(false).shouldBeTrue   // Assertion fails
 *
 * AtomicBoolean(3 + 3 == 6).shouldBeTrue    // Assertion passes
 * AtomicBoolean(3 + 3 == 42).shouldBeTrue   // Assertion fails
 * ```
 * @see [AtomicBoolean.shouldNotBeFalse]
 * @see [AtomicBoolean.shouldBeFalse]
 */
fun AtomicBoolean.shouldBeTrue() = get() shouldBe true

/**
 * Asserts that this [AtomicBoolean] is not true
 *
 * Verifies that this [AtomicBoolean] or AtomicBoolean expression is not true.
 * Opposite of [AtomicBoolean.shouldBeTrue]
 *
 * ```
 * AtomicBoolean(false).shouldNotBeTrue   // Assertion passes
 * AtomicBoolean(true).shouldNotBeTrue    // Assertion fails
 *
 * AtomicBoolean(3 + 3 == 42).shouldNotBeTrue   // Assertion passes
 * AtomicBoolean(3 + 3 == 6).shouldNotBeTrue    // Assertion fails
 * ```
 * @see [AtomicBoolean.shouldBeFalse]
 * @see [AtomicBoolean.shouldNotBeFalse]
 */
fun AtomicBoolean.shouldNotBeTrue() = get() shouldNotBe true

/**
 * Asserts that this [AtomicBoolean] is false
 *
 * Verifies that this [AtomicBoolean] or AtomicBoolean expression is false.
 * Opposite of [AtomicBoolean.shouldNotBeFalse]
 *
 * ```
 * AtomicBoolean(false).shouldBeFalse  // Assertion passes
 * AtomicBoolean(true).shouldBeFalse   // Assertion fails
 *
 * AtomicBoolean(3 + 3 == 42).shouldBeFalse  // Assertion passes
 * AtomicBoolean(3 + 3 == 6).shouldBeFalse   // Assertion fails
 * ```
 * @see [AtomicBoolean.shouldNotBeTrue]
 * @see [AtomicBoolean.shouldBeTrue]
 */
fun AtomicBoolean.shouldBeFalse() = get() shouldBe false

/**
 * Asserts that this [AtomicBoolean] is not false
 *
 * Verifies that this [AtomicBoolean] or AtomicBoolean expression is not false.
 * Opposite of [AtomicBoolean.shouldBeFalse]
 *
 * ```
 * AtomicBoolean(true).shouldNotBeFalse   // Assertion passes
 * AtomicBoolean(false).shouldNotBeFalse  // Assertion fails
 *
 * AtomicBoolean(3 + 3 == 6).shouldNotBeFalse   // Assertion passes
 * AtomicBoolean(3 + 3 == 42).shouldNotBeFalse  // Assertion fails
 * ```
 * @see [AtomicBoolean.shouldBeTrue]
 * @see [AtomicBoolean.shouldNotBeTrue]
 */
fun AtomicBoolean.shouldNotBeFalse() = get() shouldNotBe false
