package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
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

infix fun <T> Array<T>.shouldHaveSingleElement(p: (T) -> Boolean) = asList().shouldHaveSingleElement(p)
infix fun <T> Collection<T>.shouldHaveSingleElement(t: T) = this should singleElement(t)
infix fun <T> Collection<T>.shouldHaveSingleElement(p: (T) -> Boolean) = this should singleElement(p)
infix fun <T> Iterable<T>.shouldNotHaveSingleElement(t: T) = toList().shouldNotHaveSingleElement(t)
infix fun <T> Array<T>.shouldNotHaveSingleElement(t: T) = asList().shouldNotHaveSingleElement(t)
infix fun <T> Collection<T>.shouldNotHaveSingleElement(t: T) = this shouldNot singleElement(t)

fun <T> singleElement(t: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult {
      return if (value.size == 1) {
         ComparableMatcherResult(
            passed = value.single() == t,
            failureMessageFn = { "Collection should be a single element containing $t" },
            negatedFailureMessageFn = { "Collection should not be a single element of $t" },
            actual = value.single().print().value,
            expected = t.print().value,
         )
      } else {
         MatcherResult(
            passed = false,
            failureMessageFn = { "Collection should be a single element of $t but has ${value.size} elements: ${value.print().value}" },
            negatedFailureMessageFn = { "Collection should not be a single element of $t" },
         )
      }
   }
}

fun <T> singleElement(p: (T) -> Boolean): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult {
      val filteredValue: List<T> = value.filter(p)
      return MatcherResult(
         filteredValue.size == 1,
         { "Collection should have a single element by a given predicate but has ${filteredValue.size} elements: ${value.print().value}" },
         { "Collection should not have a single element by a given predicate" }
      )
   }
}
