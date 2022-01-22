package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.comparators.Comparator
import io.kotest.comparators.Comparators
import io.kotest.comparators.equality
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

// Infix
infix fun <T> Iterable<T>.shouldNotContain(t: T): Iterable<T> = shouldNotContain(t, Comparators.default())
infix fun <T> Array<T>.shouldNotContain(t: T): Array<T> = shouldNotContain(t, Comparators.default())
infix fun <T> Iterable<T>.shouldContain(t: T): Iterable<T> = shouldContain(t, Comparators.default())
infix fun <T> Array<T>.shouldContain(t: T): Array<T> = shouldContain(t, Comparators.default())

// Should not
fun <T> Iterable<T>.shouldNotContain(t: T, comparator: Comparator<T>): Iterable<T> = apply {
   toList() shouldNot contain(t, comparator)
}

fun <T> Array<T>.shouldNotContain(t: T, comparator: Comparator<T>): Array<T> = apply {
   asList().shouldNotContain(t, comparator)
}

// Should
fun <T> Iterable<T>.shouldContain(t: T, comparator: Comparator<T>): Iterable<T> = apply {
   toList() should contain(t, comparator)
}

fun <T> Array<T>.shouldContain(t: T, comparator: Comparator<T>): Array<T> = apply {
   asList().shouldContain(t, comparator)
}

// Matcher
fun <T, C : Collection<T>> contain(t: T, comparator: Comparator<T> = Comparators.default()) = object : Matcher<C> {
   override fun test(value: C) = MatcherResult(
      value.any { comparator.matches(it, t).passed() },
      {
         "Collection should contain element ${t.print().value} based on ${comparator.name()}; " +
            "listing some elements ${value.take(5)}"
      },
      { "Collection should not contain element ${t.print().value} based on ${comparator.name()}" }
   )
}
