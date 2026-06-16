package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.MatcherResultBuilder
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <T, I : Iterable<T>> I.shouldHaveSingleElement(t: T): I {
   toList().shouldHaveSingleElement(t)
   return this
}

infix fun <T> Array<T>.shouldHaveSingleElement(t: T): Array<T> {
   asList().shouldHaveSingleElement(t)
   return this
}

infix fun <T, I : Iterable<T>> I.shouldHaveSingleElement(p: (T) -> Boolean): I {
   toList().shouldHaveSingleElement(p)
   return this
}

infix fun <T> Array<T>.shouldHaveSingleElement(p: (T) -> Boolean): Array<T> {
   asList().shouldHaveSingleElement(p)
   return this
}

infix fun <T, C : Collection<T>> C.shouldHaveSingleElement(t: T): C {
   this should singleElement(t)
   return this
}

infix fun <T, C : Collection<T>> C.shouldHaveSingleElement(p: (T) -> Boolean): C {
   this should singleElement(p)
   return this
}
infix fun <T, I : Iterable<T>> I.shouldNotHaveSingleElement(t: T): I {
   toList().shouldNotHaveSingleElement(t)
   return this
}

infix fun <T> Array<T>.shouldNotHaveSingleElement(t: T): Array<T> {
   asList().shouldNotHaveSingleElement(t)
   return this
}

infix fun <T, C : Collection<T>> C.shouldNotHaveSingleElement(t: T): C {
   this shouldNot singleElement(t)
   return this
}

fun <T> singleElement(t: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult {
      return if (value.size == 1) {
         MatcherResultBuilder.create(value.single() == t)
            .withFailureMessage { "Collection should be a single element containing $t" }
            .withNegatedFailureMessage { "Collection should not be a single element of $t" }
            .withValues(expected = { t.print() }, actual = { value.single().print() })
            .build()
      } else {
         val elementFoundAtIndexes = value.mapIndexedNotNull { index, element ->
            if (element == t) index else null
         }
         val foundAtMessage = {
            if (elementFoundAtIndexes.isEmpty()) "Element not found in collection"
            else "Element found at index(es): ${elementFoundAtIndexes.print().value}"
         }
         MatcherResultBuilder.create(false)
            .withFailureMessage { "Collection should be a single element of $t but has ${value.size} elements: ${value.print().value}. ${foundAtMessage()}." }
            .withNegatedFailureMessage { "Collection should not be a single element of $t" }
            .build()
      }
   }
}

fun <T> singleElement(p: (T) -> Boolean): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult {
      val indexesOfMatchingElements = value.mapIndexedNotNull { index, element ->
         if (p(element)) index else null
      }
      val mismatchDescription = {
         when (indexesOfMatchingElements.size) {
            0 -> "no elements matched"
            1 -> ""
            else -> "elements with the following indexes matched: ${indexesOfMatchingElements.print().value}"
         }
      }
      return MatcherResult(
         indexesOfMatchingElements.size == 1,
         { "Collection should have a single element by a given predicate, but ${mismatchDescription()}, and the whole collection was: ${value.print().value}" },
         { "Collection should not have a single element by a given predicate" }
      )
   }
}
