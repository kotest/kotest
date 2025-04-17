package io.kotest.matchers.short

import io.kotest.matchers.Matcher
import io.kotest.matchers.comparables.between
import io.kotest.matchers.shouldBe

@Deprecated(
   "Short-specific assertion is getting replaced with a new Comparable assertion of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Short import `io.kotest.matchers.short.shouldBeBetween` manually.",
   ReplaceWith("shouldBeBetween(lower, upper)", "io.kotest.matchers.comparables.shouldBeBetween")
)
fun Short.shouldBeBetween(lower: Short, upper: Short): Short {
   this shouldBe between(lower, upper)
   return this
}

@Deprecated(
   "Short-specific matcher is getting replaced with a new Comparable matcher of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Short import `io.kotest.matchers.short.between` manually.",
   ReplaceWith("between(lower, upper)", "io.kotest.matchers.comparables.between")
)
fun between(lower: Short, upper: Short): Matcher<Short> = between(lower, upper)
