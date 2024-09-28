package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

infix fun <T : Comparable<T>> Array<T>.shouldHaveUpperBound(t: T): Array<T> {
   asList().shouldHaveUpperBound(t)
   return this
}

infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveUpperBound(t: T): C {
   this should haveUpperBound(t)
   return this
}

infix fun <T : Comparable<T>, I : Iterable<T>> I.shouldHaveUpperBound(t: T): I {
   toList().shouldHaveUpperBound(t)
   return this
}

fun <T : Comparable<T>, C : Collection<T>> haveUpperBound(t: T) = object : Matcher<C> {
   override fun test(value: C): MatcherResult {
      val violatingElements = value.filter { it > t }
      return MatcherResult(
         violatingElements.isEmpty(),
         { "Collection should have upper bound $t, but the following elements are above it: ${violatingElements.print().value}" },
         { "Collection should not have upper bound $t" })
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
