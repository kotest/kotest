package io.kotest.assertions.eq

/**
 * A [Eq] typeclass compares two values for equality, returning an [AssertionError] if they are
 * not equal, or null if they are equal.
 *
 * This equality typeclass is at the heart of the shouldBe matcher.
 */
interface Eq<T> {
   fun equals(actual: T, expected: T): Throwable?
}

/**
 * Locates the applicable [Eq] for the inputs, and invokes it, returning the error if any.
 */
fun <T> eq(actual: T, expected: T): Throwable? = when {
   actual is Map<*, *> && expected is Map<*, *> -> MapEq.equals(actual, expected)
   actual is Throwable && expected is Throwable -> ThrowableEq.equals(actual, expected)
   actual is Regex && expected is Regex -> RegexEq.equals(actual, expected)
   actual is String && expected is String -> StringEq.equals(actual, expected)
   actual is Number && expected is Number -> NumberEq.equals(actual, expected)
   else -> DefaultEq.equals(actual as Any, expected as Any)
}
