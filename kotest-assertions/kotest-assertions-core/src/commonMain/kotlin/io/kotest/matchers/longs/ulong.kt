package io.kotest.matchers.longs

import io.kotest.matchers.Matcher
import io.kotest.matchers.comparables.between
import io.kotest.matchers.shouldBe

@Deprecated(
   "ULong-specific assertion is getting replaced with a new Comparable assertion of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the ULong import `io.kotest.matchers.longs.shouldBeBetween` manually.",
   ReplaceWith("shouldBeBetween(lower, upper)", "io.kotest.matchers.comparables.shouldBeBetween")
)
fun ULong.shouldBeBetween(lower: ULong, upper: ULong): ULong {
   this shouldBe between(lower, upper)
   return this
}

@Deprecated(
   "ULong-specific matcher is getting replaced with a new Comparable matcher of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the ULong import `io.kotest.matchers.longs.between` manually.",
   ReplaceWith("between(lower, upper)", "io.kotest.matchers.comparables.between")
)
fun between(lower: ULong, upper: ULong): Matcher<ULong> = between(lower, upper)
