package io.kotest.matchers.bytes

import io.kotest.matchers.Matcher
import io.kotest.matchers.comparables.between
import io.kotest.matchers.shouldBe

@Deprecated(
   "UByte-specific assertion is getting replaced with a new Comparable assertion of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the UByte import `io.kotest.matchers.bytes.shouldBeBetween` manually.",
   ReplaceWith("shouldBeBetween(lower, upper)", "io.kotest.matchers.comparables.shouldBeBetween")
)
fun UByte.shouldBeBetween(lower: UByte, upper: UByte): UByte {
   this shouldBe between(lower, upper)
   return this
}

@Deprecated(
   "UByte-specific matcher is getting replaced with a new Comparable matcher of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the UByte import `io.kotest.matchers.bytes.between` manually.",
   ReplaceWith("between(lower, upper)", "io.kotest.matchers.comparables.between")
)
fun between(lower: UByte, upper: UByte): Matcher<UByte> = between(lower, upper)
