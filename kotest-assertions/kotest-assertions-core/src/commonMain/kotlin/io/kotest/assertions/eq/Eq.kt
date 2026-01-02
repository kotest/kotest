package io.kotest.assertions.eq

/**
 * A [Eq] typeclass compares two values for equality, returning a [Throwable] if they are
 * not equal, or null if they are equal.
 *
 * This equality typeclass is at the heart of the shouldBe matcher.
 *
 */
interface Eq<T> {
   /**
    * @param context tracks visited object pairs to prevent infinite recursion on cyclic references
    */
   fun equals(actual: T, expected: T, context: EqContext): Throwable?
}
