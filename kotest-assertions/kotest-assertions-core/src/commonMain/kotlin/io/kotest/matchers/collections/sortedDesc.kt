package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

fun <T : Comparable<T>> Array<T>.shouldBeSortedDescending(): Array<T> {
   asList().shouldBeSortedDescending()
   return this
}

infix fun <T, E : Comparable<E>> Array<T>.shouldBeSortedDescendingBy(transform: (T) -> E): Array<T> {
   asList().shouldBeSortedDescendingBy(transform)
   return this
}

fun <T : Comparable<T>> List<T>.shouldBeSortedDescending(): List<T> {
   this should beSortedDescending()
   return this
}

infix fun <T, E : Comparable<E>> List<T>.shouldBeSortedDescendingBy(transform: (T) -> E): List<T> {
   this should beSortedDescendingBy(transform)
   return this
}

fun <T : Comparable<T>, I : Iterable<T>> I.shouldBeSortedDescending(): I {
   toList().shouldBeSortedDescending()
   return this
}

infix fun <T, I : Iterable<T>, E : Comparable<E>> I.shouldBeSortedDescendingBy(transform: (T) -> E): I {
   toList().shouldBeSortedDescendingBy(transform)
   return this
}

fun <T : Comparable<T>> beSortedDescending(): Matcher<List<T>> = sortedDescending()

fun <T : Comparable<T>> sortedDescending(): Matcher<List<T>> = sortedDescendingBy { it }

fun <T, E : Comparable<E>> beSortedDescendingBy(transform: (T) -> E): Matcher<List<T>> = sortedDescendingBy(transform)

fun <T, E : Comparable<E>> sortedDescendingBy(transform: (T) -> E): Matcher<List<T>> = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      val failure =
         value.withIndex().firstOrNull { (i, it) -> i != value.lastIndex && transform(it) < transform(value[i + 1]) }
      val elementMessage = {
         when (failure) {
            null -> ""
            else -> ". Element ${failure.value} at index ${failure.index} was less than element ${value[failure.index + 1]}"
         }
      }
      return MatcherResult(
         failure == null,
         { "List ${value.print().value} should be sorted${elementMessage()}" },
         { "List ${value.print().value} should not be sorted" }
      )
   }
}
