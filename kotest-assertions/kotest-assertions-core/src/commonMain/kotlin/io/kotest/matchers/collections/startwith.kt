package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <T> Iterable<T>.shouldStartWith(element: T) = toList().shouldStartWith(listOf(element))
infix fun <T> Iterable<T>.shouldStartWith(slice: Iterable<T>) = toList().shouldStartWith(slice.toList())
infix fun <T> Iterable<T>.shouldStartWith(slice: Array<T>) = toList().shouldStartWith(slice.asList())

infix fun <T> Array<T>.shouldStartWith(element: T) = asList().shouldStartWith(listOf(element))
infix fun <T> Array<T>.shouldStartWith(slice: Collection<T>) = asList().shouldStartWith(slice)
infix fun <T> Array<T>.shouldStartWith(slice: Array<T>) = asList().shouldStartWith(slice.asList())

infix fun <T> List<T>.shouldStartWith(element: T) = this should startWith(listOf(element))
infix fun <T> List<T>.shouldStartWith(slice: Collection<T>) = this should startWith(slice)

infix fun <T> Iterable<T>.shouldNotStartWith(element: T) = toList().shouldNotStartWith(listOf(element))
infix fun <T> Iterable<T>.shouldNotStartWith(slice: Iterable<T>) = toList().shouldNotStartWith(slice.toList())
infix fun <T> Iterable<T>.shouldNotStartWith(slice: Array<T>) = toList().shouldNotStartWith(slice.asList())

infix fun <T> Array<T>.shouldNotStartWith(element: T) = asList().shouldNotStartWith(listOf(element))
infix fun <T> Array<T>.shouldNotStartWith(slice: Collection<T>) = asList().shouldNotStartWith(slice)
infix fun <T> Array<T>.shouldNotStartWith(slice: Array<T>) = asList().shouldNotStartWith(slice.asList())

infix fun <T> List<T>.shouldNotStartWith(element: T) = this shouldNot startWith(listOf(element))
infix fun <T> List<T>.shouldNotStartWith(slice: Collection<T>) = this shouldNot startWith(slice)

fun <T> startWith(slice: Collection<T>) = object : Matcher<List<T>> {
   override fun test(value: List<T>) =
      MatcherResult(
         value.subList(0, slice.size) == slice,
         { "List should start with ${slice.printed().value} but was ${value.take(slice.size).printed().value}" },
         { "List should not start with ${slice.printed().value}" }
      )
}

infix fun <T> Iterable<T>.shouldEndWith(element: T) = toList().shouldEndWith(listOf(element))
infix fun <T> Iterable<T>.shouldEndWith(slice: Iterable<T>) = toList().shouldEndWith(slice.toList())
infix fun <T> Iterable<T>.shouldEndWith(slice: Array<T>) = toList().shouldEndWith(slice.asList())

infix fun <T> Array<T>.shouldEndWith(element: T) = asList().shouldEndWith(listOf(element))
infix fun <T> Array<T>.shouldEndWith(slice: Collection<T>) = asList().shouldEndWith(slice)
infix fun <T> Array<T>.shouldEndWith(slice: Array<T>) = asList().shouldEndWith(slice.asList())

infix fun <T> List<T>.shouldEndWith(element: T) = this.shouldEndWith(listOf(element))
infix fun <T> List<T>.shouldEndWith(slice: Collection<T>) = this should endWith(slice)
infix fun <T> List<T>.shouldEndWith(slice: Array<T>) = this.shouldEndWith(slice.toList())

infix fun <T> Iterable<T>.shouldNotEndWith(element: T) = toList().shouldNotEndWith(listOf(element))
infix fun <T> Iterable<T>.shouldNotEndWith(slice: Iterable<T>) = toList().shouldNotEndWith(slice.toList())
infix fun <T> Iterable<T>.shouldNotEndWith(slice: Array<T>) = toList().shouldNotEndWith(slice.asList())

infix fun <T> Array<T>.shouldNotEndWith(element: T) = asList().shouldNotEndWith(listOf(element))
infix fun <T> Array<T>.shouldNotEndWith(slice: Collection<T>) = asList().shouldNotEndWith(slice)
infix fun <T> Array<T>.shouldNotEndWith(slice: Array<T>) = asList().shouldNotEndWith(slice.asList())

infix fun <T> List<T>.shouldNotEndWith(element: T) = this shouldNot endWith(listOf(element))
infix fun <T> List<T>.shouldNotEndWith(slice: Collection<T>) = this shouldNot endWith(slice)

fun <T> endWith(slice: Collection<T>) = object : Matcher<List<T>> {
   override fun test(value: List<T>) =
      MatcherResult(
         value.subList(value.size - slice.size, value.size) == slice,
         { "List should end with ${slice.printed().value} but was ${value.take(slice.size).printed().value}" },
         { "List should not end with ${slice.printed().value}" }
      )
}
