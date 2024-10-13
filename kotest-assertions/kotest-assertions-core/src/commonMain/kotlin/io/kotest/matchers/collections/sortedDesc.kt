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

fun <T : Comparable<T>> beSortedDescending(): Matcher<Iterable<T>> = beSortedDescending(null)

fun <T : Comparable<T>> sortedDescending(): Matcher<Iterable<T>> = beSortedDescending()

fun <T, E : Comparable<E>> beSortedDescendingBy(transform: (T) -> E): Matcher<Iterable<T>> =
   beSortedDescendingWith(null) { a, b -> transform(a).compareTo(transform(b)) }

fun <T, E : Comparable<E>> sortedDescendingBy(transform: (T) -> E): Matcher<Iterable<T>> =
   beSortedDescendingBy(transform)

internal fun <T : Comparable<T>> beSortedDescending(name: String?): Matcher<Iterable<T>> =
   beSortedDescendingWith(name) { a, b -> a.compareTo(b) }

private fun <T, I : Iterable<T>> beSortedDescendingWith(name: String?, comparator: Comparator<T>): Matcher<I> =
   object : Matcher<I> {
      override fun test(value: I): MatcherResult {
         val name = name ?: value.containerName()
         val positiveResult = MatcherResult(
            true,
            { "$name should be sorted descending" },
            { "$name should not be sorted descending" }
         )

         value.zippedWithNext(
            onEmpty = { return positiveResult },
            onSingle = { return positiveResult }
         ) { a, b ->
            if (comparator.compare(a.value, b.value) < 0) {
               return MatcherResult(
                  false,
                  { "$name should be sorted in descending order. Element ${a.value.print().value} at index ${a.index} was smaller than element ${b.value.print().value} at index ${b.index}" },
                  { "$name should not be sorted descending" }
               )
            }
         }

         return positiveResult
      }
   }

internal inline fun <T> Iterable<T>.zippedWithNext(
   onEmpty: () -> Unit,
   onSingle: (T) -> Unit,
   onEach: (IndexedValue<T>, IndexedValue<T>) -> Unit
) {
   val iterator = iterator().withIndex()
   if (!iterator.hasNext()) {
      onEmpty()
      return
   }

   var first = iterator.next()

   if (!iterator.hasNext()) {
      onSingle(first.value)
      return
   }

   while (iterator.hasNext()) {
      val second = iterator.next()
      onEach(first, second)
      first = second
   }
}
