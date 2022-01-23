package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.equals.EqualityVerifier
import io.kotest.equals.EqualityVerifiers
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

// Infix
infix fun <T> Iterable<T>.shouldNotContain(t: T): Iterable<T> = shouldNotContain(t, EqualityVerifiers.default())
infix fun <T> Array<T>.shouldNotContain(t: T): Array<T> = shouldNotContain(t, EqualityVerifiers.default())
infix fun <T> Iterable<T>.shouldContain(t: T): Iterable<T> = shouldContain(t, EqualityVerifiers.default())
infix fun <T> Array<T>.shouldContain(t: T): Array<T> = shouldContain(t, EqualityVerifiers.default())

// Should not
fun <T> Iterable<T>.shouldNotContain(t: T, comparator: EqualityVerifier<T>): Iterable<T> = apply {
   toList() shouldNot contain(t, comparator)
}

fun <T> Array<T>.shouldNotContain(t: T, comparator: EqualityVerifier<T>): Array<T> = apply {
   asList().shouldNotContain(t, comparator)
}

// Should
fun <T> Iterable<T>.shouldContain(t: T, comparator: EqualityVerifier<T>): Iterable<T> = apply {
   toList() should contain(t, comparator)
}

fun <T> Array<T>.shouldContain(t: T, comparator: EqualityVerifier<T>): Array<T> = apply {
   asList().shouldContain(t, comparator)
}

// Matcher
fun <T, C : Collection<T>> contain(t: T, comparator: EqualityVerifier<T> = EqualityVerifiers.default()) = object : Matcher<C> {
   override fun test(value: C) = MatcherResult(
      value.any { comparator.verify(it, t).passed() },
      {
         "Collection should contain element ${t.print().value} based on ${comparator.name()}; " +
            "listing some elements ${value.take(5)}"
      },
      { "Collection should not contain element ${t.print().value} based on ${comparator.name()}" }
   )
}
