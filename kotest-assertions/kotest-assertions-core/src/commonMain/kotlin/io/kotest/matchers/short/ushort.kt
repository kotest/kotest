package io.kotest.matchers.short

import io.kotest.matchers.Matcher
import io.kotest.matchers.comparables.between
import io.kotest.matchers.shouldBe

@Deprecated(
   "UShort-specific assertion is getting replaced with a new Comparable assertion of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the UShort import `io.kotest.matchers.short.shouldBeBetween` manually.",
   ReplaceWith("shouldBeBetween(lower, upper)", "io.kotest.matchers.comparables.shouldBeBetween")
)
fun UShort.shouldBeBetween(lower: UShort, upper: UShort): UShort {
   this shouldBe between(lower, upper)
   return this
}

@Deprecated(
   "UShort-specific matcher is getting replaced with a new Comparable matcher of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the UShort import `io.kotest.matchers.short.between` manually.",
   ReplaceWith("between(lower, upper)", "io.kotest.matchers.comparables.between")
)
fun between(lower: UShort, upper: UShort): Matcher<UShort> = between(lower, upper)
