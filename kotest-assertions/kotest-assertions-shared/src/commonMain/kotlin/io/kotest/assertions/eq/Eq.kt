package io.kotest.assertions.eq

/**
 * A [Eq] typeclass compares two values for equality, returning a [Throwable] if they are
 * not equal, or null if they are equal.
 *
 * This equality typeclass is at the heart of the shouldBe matcher.
 */
interface Eq<T> {
   fun equals(actual: T, expected: T, strictNumberEq: Boolean): Throwable?
}
