package io.kotest.matchers

import io.kotest.matchers.types.shouldHaveSameHashCodeAs
import io.kotest.matchers.types.haveSameHashCodeAs

@Deprecated("Moved to io.kotest.matchers.types. This Will be removed in 4.5",
   ReplaceWith("this.shouldHaveSameHashCodeAs(other)",
      "io.kotest.matchers.types.shouldHaveSameHashCodeAs"))
fun Any.shouldHaveSameHashCodeAs(other: Any) = this.shouldHaveSameHashCodeAs(other)

@Deprecated("Moved to io.kotest.matchers.types. This Will be removed in 4.5",
   ReplaceWith("this shouldNot haveSameHashCodeAs(other)",
      "io.kotest.matchers.types.haveSameHashCodeAs"))
fun Any.shouldNotHaveSameHashCodeAs(other: Any) = this shouldNot haveSameHashCodeAs(other)

@Deprecated("Moved to io.kotest.matchers.types. This Will be removed in 4.5",
   ReplaceWith("haveSameHashCodeAs(other)", "io.kotest.matchers.types.haveSameHashCodeAs"))
fun haveSameHashCodeAs(other: Any) = haveSameHashCodeAs(other)
