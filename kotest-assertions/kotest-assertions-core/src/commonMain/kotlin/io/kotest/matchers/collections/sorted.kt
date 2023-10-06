package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <T> beSortedWith(comparator: Comparator<in T>): Matcher<List<T>> = sortedWith(comparator)
fun <T> beSortedWith(cmp: (T, T) -> Int): Matcher<List<T>> = sortedWith(cmp)
fun <T> sortedWith(comparator: Comparator<in T>): Matcher<List<T>> = sortedWith { a, b ->
   comparator.compare(a, b)
}

fun <T> sortedWith(cmp: (T, T) -> Int): Matcher<List<T>> = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      val failure = value.withIndex().firstOrNull { (i, it) -> i != value.lastIndex && cmp(it, value[i + 1]) > 0 }
      val snippet = value.joinToString(",", limit = 10)
      val elementMessage = when (failure) {
         null -> ""
         else -> ". Element ${failure.value} at index ${failure.index} shouldn't precede element ${value[failure.index + 1]}"
      }
      return MatcherResult(
         failure == null,
         { "List [$snippet] should be sorted$elementMessage" },
         { "List [$snippet] should not be sorted" })
   }
}

fun <T : Comparable<T>> Iterable<T>.shouldBeSorted(): Iterable<T> {
   toList().shouldBeSorted()
   return this
}

fun <T : Comparable<T>> Array<T>.shouldBeSorted(): Array<T> {
   asList().shouldBeSorted()
   return this
}

fun <T : Comparable<T>> List<T>.shouldBeSorted(): List<T> {
   this should beSorted()
   return this
}

fun <T : Comparable<T>> Iterable<T>.shouldBeSortedDescending(): Iterable<T> {
   toList().shouldBeSortedDescending()
   return this
}

fun <T : Comparable<T>> Array<T>.shouldBeSortedDescending(): Array<T> {
   asList().shouldBeSortedDescending()
   return this
}

fun <T : Comparable<T>> List<T>.shouldBeSortedDescending(): List<T> {
   this should beSortedDescending()
   return this
}

fun <T : Comparable<T>> Iterable<T>.shouldNotBeSorted(): Iterable<T> {
   toList().shouldNotBeSorted()
   return this
}

fun <T : Comparable<T>> Array<T>.shouldNotBeSorted(): Array<T> {
   asList().shouldNotBeSorted()
   return this
}

fun <T : Comparable<T>> List<T>.shouldNotBeSorted(): List<T> {
   this shouldNot beSorted()
   return this
}

infix fun <T, E : Comparable<E>> Iterable<T>.shouldBeSortedBy(transform: (T) -> E): Iterable<T> {
   toList().shouldBeSortedBy(transform)
   return this
}

infix fun <T, E : Comparable<E>> Array<T>.shouldBeSortedBy(transform: (T) -> E): Array<T> {
   asList().shouldBeSortedBy(transform)
   return this
}

infix fun <T, E : Comparable<E>> List<T>.shouldBeSortedBy(transform: (T) -> E): List<T> {
   this should beSortedBy(transform)
   return this
}

infix fun <T, E : Comparable<E>> Iterable<T>.shouldBeSortedDescendingBy(transform: (T) -> E): Iterable<T> {
   toList().shouldBeSortedDescendingBy(transform)
   return this
}

infix fun <T, E : Comparable<E>> Array<T>.shouldBeSortedDescendingBy(transform: (T) -> E): Array<T> {
   asList().shouldBeSortedDescendingBy(transform)
   return this
}

infix fun <T, E : Comparable<E>> List<T>.shouldBeSortedDescendingBy(transform: (T) -> E): List<T> {
   this should beSortedDescendingBy(transform)
   return this
}

infix fun <T, E : Comparable<E>> Iterable<T>.shouldNotBeSortedBy(transform: (T) -> E): Iterable<T> {
   toList().shouldNotBeSortedBy(transform)
   return this
}

infix fun <T, E : Comparable<E>> Array<T>.shouldNotBeSortedBy(transform: (T) -> E): Array<T> {
   asList() shouldNotBeSortedBy transform
   return this
}

infix fun <T, E : Comparable<E>> List<T>.shouldNotBeSortedBy(transform: (T) -> E): List<T> {
   this shouldNot beSortedBy(transform)
   return this
}

infix fun <T> Iterable<T>.shouldBeSortedWith(comparator: Comparator<in T>): Iterable<T> {
   toList().shouldBeSortedWith(comparator)
   return this
}

infix fun <T> Array<T>.shouldBeSortedWith(comparator: Comparator<in T>): Array<T> {
   asList().shouldBeSortedWith(comparator)
   return this
}

infix fun <T> List<T>.shouldBeSortedWith(comparator: Comparator<in T>): List<T> {
   this should beSortedWith(comparator)
   return this
}

infix fun <T> Iterable<T>.shouldNotBeSortedWith(comparator: Comparator<in T>): Iterable<T> {
   toList().shouldNotBeSortedWith(comparator)
   return this
}

infix fun <T> Array<T>.shouldNotBeSortedWith(comparator: Comparator<in T>): Array<T> {
   asList().shouldNotBeSortedWith(comparator)
   return this
}

infix fun <T> List<T>.shouldNotBeSortedWith(comparator: Comparator<in T>): List<T> {
   this shouldNot beSortedWith(comparator)
   return this
}

infix fun <T> Iterable<T>.shouldBeSortedWith(cmp: (T, T) -> Int): Iterable<T> {
   toList().shouldBeSortedWith(cmp)
   return this
}

infix fun <T> Array<T>.shouldBeSortedWith(cmp: (T, T) -> Int): Array<T> {
   asList().shouldBeSortedWith(cmp)
   return this
}

infix fun <T> List<T>.shouldBeSortedWith(cmp: (T, T) -> Int): List<T> {
   this should beSortedWith(cmp)
   return this
}

infix fun <T> Iterable<T>.shouldNotBeSortedWith(cmp: (T, T) -> Int): Iterable<T> {
   toList().shouldNotBeSortedWith(cmp)
   return this
}

infix fun <T> Array<T>.shouldNotBeSortedWith(cmp: (T, T) -> Int): Array<T> {
   asList().shouldNotBeSortedWith(cmp)
   return this
}

infix fun <T> List<T>.shouldNotBeSortedWith(cmp: (T, T) -> Int): List<T> {
   this shouldNot beSortedWith(cmp)
   return this
}
