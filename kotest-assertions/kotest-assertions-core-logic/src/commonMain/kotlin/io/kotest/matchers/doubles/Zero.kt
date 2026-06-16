package io.kotest.matchers.doubles

/**
 * Asserts that this [Double] is exactly zero (0.0).
 *
 * Verifies that this [Double] is exactly zero (0.0).
 *
 * Opposite of [Double.shouldNotBeZero]
 *
 * ```
 * 0.0 shouldBeZero()   // Assertion passes
 * 0.1 shouldBeZero()   // Assertion fails
 * -0.1 shouldBeZero()   // Assertion fails
 * ```
 */
fun Double.shouldBeZero(): Double {
   this shouldBeExactly 0.0
   return this
}

/**
 * Asserts that this [Double] is not zero
 *
 * Verifies that this [Double] is not zero.
 *
 * Opposite of [Double.shouldBeZero]
 *
 * ```
 * 0.1 shouldNotBeZero()   // Assertion passes
 * -0.1 shouldNotBeZero()   // Assertion passes
 * 0.0 shouldNotBeZero()   // Assertion fails
 * ```
 */
fun Double.shouldNotBeZero(): Double {
   this shouldNotBeExactly 0.0
   return this
}
