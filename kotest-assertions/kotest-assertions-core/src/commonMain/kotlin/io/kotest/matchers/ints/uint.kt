package io.kotest.matchers.ints

import io.kotest.matchers.Matcher
import io.kotest.matchers.comparables.between
import io.kotest.matchers.shouldBe

@Deprecated(
   "UInt-specific assertion is getting replaced with a new Comparable assertion of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the UInt import `io.kotest.matchers.ints.shouldBeBetween` manually.",
   ReplaceWith("shouldBeBetween(lower, upper)", "io.kotest.matchers.comparables.shouldBeBetween")
)
fun UInt.shouldBeBetween(lower: UInt, upper: UInt): UInt {
   this shouldBe between(lower, upper)
   return this
}

@Deprecated(
   "UInt-specific matcher is getting replaced with a new Comparable matcher of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the UInt import `io.kotest.matchers.ints.between` manually.",
   ReplaceWith("between(lower, upper)", "io.kotest.matchers.comparables.between")
)
fun between(lower: UInt, upper: UInt): Matcher<UInt> = between(lower, upper)
