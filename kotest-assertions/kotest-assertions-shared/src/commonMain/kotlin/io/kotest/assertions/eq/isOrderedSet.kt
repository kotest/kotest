package io.kotest.assertions.eq

/**
 * Allows platform-specific ordered sets to be compared using [IterableEq]
 */
expect fun isOrderedSet(item: Iterable<*>): Boolean
