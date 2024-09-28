package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

infix fun <T : Comparable<T>> Array<T>.shouldHaveUpperBound(t: T): Array<T> {
   asList() should haveUpperBound(t, "Array")
   return this
}

infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveUpperBound(t: T): C {
   this should haveUpperBound(t, null)
   return this
}

infix fun <T : Comparable<T>, I : Iterable<T>> I.shouldHaveUpperBound(t: T): I {
   this should haveUpperBound(t, null)
   return this
}

fun <T : Comparable<T>, C : Collection<T>> haveUpperBound(t: T): Matcher<C> = haveUpperBound(t, null)

private fun <T : Comparable<T>, I : Iterable<T>> haveUpperBound(t: T, name: String?): Matcher<I> = object : Matcher<I> {
   override fun test(value: I): MatcherResult {
      val name = name ?: value.containerName()
      val violatingElements = value.filter { it > t }
      return MatcherResult(
         violatingElements.isEmpty(),
         { "$name should have upper bound $t, but the following elements are above it: ${violatingElements.print().value}" },
         { "$name should not have upper bound $t" })
   }

   private fun Iterable<*>.containerName(): String {
      return when (this) {
         is List -> "List"
         is Set -> "Set"
         is Map<*, *> -> "Map"
         is ClosedRange<*>, is OpenEndRange<*> -> "Range"
         is Collection -> "Collection"
         else -> "Iterable"
      }
   }
}

infix fun <T : Comparable<T>> Array<T>.shouldHaveLowerBound(t: T): Array<T> {
   asList().shouldHaveLowerBound(t)
   return this
}

infix fun <T : Comparable<T>, I : Iterable<T>> I.shouldHaveLowerBound(t: T): I {
   toList().shouldHaveLowerBound(t)
   return this
}

infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveLowerBound(t: T): C {
   this should haveLowerBound(t)
   return this
}

fun <T : Comparable<T>, C : Collection<T>> haveLowerBound(t: T) = object : Matcher<C> {
   override fun test(value: C): MatcherResult {
      val violatingElements = value.filter { it < t }
      return MatcherResult(
         violatingElements.isEmpty(),
         { "Collection should have lower bound $t, but the following elements are below it: ${violatingElements.print().value}" },
         { "Collection should not have lower bound $t" })
   }
}
