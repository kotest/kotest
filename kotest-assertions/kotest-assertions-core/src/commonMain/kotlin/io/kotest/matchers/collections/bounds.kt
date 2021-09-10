package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

infix fun <T : Comparable<T>> Iterable<T>.shouldHaveUpperBound(t: T): Iterable<T> {
   toList().shouldHaveUpperBound(t)
   return this
}

infix fun <T : Comparable<T>> Array<T>.shouldHaveUpperBound(t: T): Array<T> {
   asList().shouldHaveUpperBound(t)
   return this
}

infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveUpperBound(t: T): C {
   this should haveUpperBound(t)
   return this
}

fun <T : Comparable<T>, C : Collection<T>> haveUpperBound(t: T) = object : Matcher<C> {
   override fun test(value: C) = MatcherResult(
      value.all { it <= t },
      { "Collection should have upper bound $t" },
      { "Collection should not have upper bound $t" })
}

infix fun <T : Comparable<T>> Iterable<T>.shouldHaveLowerBound(t: T): Iterable<T> {
   toList().shouldHaveLowerBound(t)
   return this
}

infix fun <T : Comparable<T>> Array<T>.shouldHaveLowerBound(t: T): Array<T> {
   asList().shouldHaveLowerBound(t)
   return this
}

infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveLowerBound(t: T): C {
   this should haveLowerBound(t)
   return this
}

fun <T : Comparable<T>, C : Collection<T>> haveLowerBound(t: T) = object : Matcher<C> {
   override fun test(value: C) = MatcherResult(
      value.all { t <= it },
      { "Collection should have lower bound $t" },
      { "Collection should not have lower bound $t" })
}
