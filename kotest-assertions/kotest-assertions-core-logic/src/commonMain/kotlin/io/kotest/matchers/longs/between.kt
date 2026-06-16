package io.kotest.matchers.longs

import io.kotest.matchers.Matcher
import io.kotest.matchers.comparables.between
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot

/**
 * Verifies that the given Long is between a and b inclusive.
 */
@Deprecated(
   "Long-specific assertion is getting replaced with a new Comparable assertion of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Long import `io.kotest.matchers.longs.shouldBeBetween` manually.",
   ReplaceWith("shouldBeBetween(a, b)", "io.kotest.matchers.comparables.shouldBeBetween")
)
fun Long.shouldBeBetween(a: Long, b: Long): Long {
   this shouldBe between(a, b)
   return this
}

/**
 * Verifies that the given Long is NOT between a and b inclusive.
 */
@Deprecated(
   "Long-specific assertion is getting replaced with a new Comparable assertion of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Long import `io.kotest.matchers.longs.shouldNotBeBetween` manually.",
   ReplaceWith("shouldNotBeBetween(a, b)", "io.kotest.matchers.comparables.shouldNotBeBetween")
)
fun Long.shouldNotBeBetween(a: Long, b: Long): Long {
   this shouldNot between(a, b)
   return this
}

/**
 * Verifies that the given Long is between a and b inclusive.
 */
@Deprecated(
   "Long-specific matcher is getting replaced with a new Comparable matcher of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Long import `io.kotest.matchers.longs.between` manually.",
   ReplaceWith("between(a, b)", "io.kotest.matchers.comparables.between")
)
fun between(a: Long, b: Long): Matcher<Long> = between(a, b)
