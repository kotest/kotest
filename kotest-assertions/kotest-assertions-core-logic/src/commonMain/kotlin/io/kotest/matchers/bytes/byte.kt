package io.kotest.matchers.bytes

import io.kotest.matchers.Matcher
import io.kotest.matchers.comparables.between
import io.kotest.matchers.shouldBe

/**
 * Match that verifies a given [Byte] has a value between [lower, upper] (inclusive, inclusive).
 */
@Deprecated(
   "Byte-specific assertion is getting replaced with a new Comparable assertion of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Byte import `io.kotest.matchers.bytes.shouldBeBetween` manually.",
   ReplaceWith("shouldBeBetween(lower, upper)", "io.kotest.matchers.comparables.shouldBeBetween")
)
fun Byte.shouldBeBetween(lower: Byte, upper: Byte): Byte {
   this shouldBe between(lower, upper)
   return this
}

/**
 * Match that verifies a given [Byte] has a value between [lower, upper] (inclusive, inclusive).
 */
@Deprecated(
   "Byte-specific matcher is getting replaced with a new Comparable matcher of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Byte import `io.kotest.matchers.bytes.between` manually.",
   ReplaceWith("between(lower, upper)", "io.kotest.matchers.comparables.between")
)
fun between(lower: Byte, upper: Byte): Matcher<Byte> = between(lower, upper)
