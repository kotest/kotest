package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <T> Iterable<T>.shouldHaveElementAt(index: Int, element: T) = toList().shouldHaveElementAt(index, element)
fun <T> Array<T>.shouldHaveElementAt(index: Int, element: T) = asList().shouldHaveElementAt(index, element)
fun <T> List<T>.shouldHaveElementAt(index: Int, element: T) = this should haveElementAt(index, element)

fun <T> Iterable<T>.shouldNotHaveElementAt(index: Int, element: T) = toList().shouldNotHaveElementAt(index, element)
fun <T> Array<T>.shouldNotHaveElementAt(index: Int, element: T) = asList().shouldNotHaveElementAt(index, element)
fun <T> List<T>.shouldNotHaveElementAt(index: Int, element: T) = this shouldNot haveElementAt(index, element)

fun <T, L : List<T>> haveElementAt(index: Int, element: T) = object : Matcher<L> {
   override fun test(value: L) =
      MatcherResult(
         index < value.size && value[index] == element,
         { "Collection ${value.print().value} should contain ${element.print().value} at index $index" },
         { "Collection ${value.print().value} should not contain ${element.print().value} at index $index" }
      )
}

infix fun <T> Iterable<T>.shouldExist(p: (T) -> Boolean) = toList().shouldExist(p)
infix fun <T> Array<T>.shouldExist(p: (T) -> Boolean) = asList().shouldExist(p)
infix fun <T> Collection<T>.shouldExist(p: (T) -> Boolean) = this should exist(p)
fun <T> exist(p: (T) -> Boolean) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.any { p(it) },
      { "Collection ${value.print().value} should contain an element that matches the predicate $p" },
      { "Collection ${value.print().value} should not contain an element that matches the predicate $p" }
   )
}

fun <T> Iterable<T>.shouldMatchInOrder(vararg assertions: (T) -> Unit) = toList().shouldMatchInOrder(assertions.toList())
fun <T> Array<T>.shouldMatchInOrder(vararg assertions: (T) -> Unit) = asList().shouldMatchInOrder(assertions.toList())
fun <T> List<T>.shouldMatchInOrder(vararg assertions: (T) -> Unit) = this.shouldMatchInOrder(assertions.toList())
infix fun <T> Iterable<T>.shouldMatchInOrder(assertions: List<(T) -> Unit>) = toList().shouldMatchInOrder(assertions)
infix fun <T> Array<T>.shouldMatchInOrder(assertions: List<(T) -> Unit>) = asList().shouldMatchInOrder(assertions)
infix fun <T> List<T>.shouldMatchInOrder(assertions: List<(T) -> Unit>) = this should matchInOrder(assertions.toList(), allowGaps = false)
fun <T> Iterable<T>.shouldNotMatchInOrder(vararg assertions: (T) -> Unit) = toList().shouldNotMatchInOrder(assertions.toList())
fun <T> Array<T>.shouldNotMatchInOrder(vararg assertions: (T) -> Unit) = asList().shouldNotMatchInOrder(assertions.toList())
fun <T> List<T>.shouldNotMatchInOrder(vararg assertions: (T) -> Unit) = this.shouldNotMatchInOrder(assertions.toList())
infix fun <T> Iterable<T>.shouldNotMatchInOrder(assertions: List<(T) -> Unit>) = toList().shouldNotMatchInOrder(assertions)
infix fun <T> Array<T>.shouldNotMatchInOrder(assertions: List<(T) -> Unit>) = asList().shouldNotMatchInOrder(assertions)
infix fun <T> List<T>.shouldNotMatchInOrder(assertions: List<(T) -> Unit>) = this shouldNot matchInOrder(assertions.toList(), allowGaps = false)

fun <T> Iterable<T>.shouldMatchInOrderSubset(vararg assertions: (T) -> Unit) = toList().shouldMatchInOrderSubset(assertions.toList())
fun <T> Array<T>.shouldMatchInOrderSubset(vararg assertions: (T) -> Unit) = asList().shouldMatchInOrderSubset(assertions.toList())
fun <T> List<T>.shouldMatchInOrderSubset(vararg assertions: (T) -> Unit) = this.shouldMatchInOrderSubset(assertions.toList())
infix fun <T> Iterable<T>.shouldMatchInOrderSubset(assertions: List<(T) -> Unit>) = toList().shouldMatchInOrderSubset(assertions)
infix fun <T> Array<T>.shouldMatchInOrderSubset(assertions: List<(T) -> Unit>) = asList().shouldMatchInOrderSubset(assertions)
infix fun <T> List<T>.shouldMatchInOrderSubset(assertions: List<(T) -> Unit>) = this should matchInOrder(assertions.toList(), allowGaps = true)
fun <T> Iterable<T>.shouldNotMatchInOrderSubset(vararg assertions: (T) -> Unit) = toList().shouldNotMatchInOrderSubset(assertions.toList())
fun <T> Array<T>.shouldNotMatchInOrderSubset(vararg assertions: (T) -> Unit) = asList().shouldNotMatchInOrderSubset(assertions.toList())
fun <T> List<T>.shouldNotMatchInOrderSubset(vararg assertions: (T) -> Unit) = this.shouldNotMatchInOrderSubset(assertions.toList())
infix fun <T> Iterable<T>.shouldNotMatchInOrderSubset(assertions: List<(T) -> Unit>) = toList().shouldNotMatchInOrderSubset(assertions)
infix fun <T> Array<T>.shouldNotMatchInOrderSubset(assertions: List<(T) -> Unit>) = asList().shouldNotMatchInOrderSubset(assertions)
infix fun <T> List<T>.shouldNotMatchInOrderSubset(assertions: List<(T) -> Unit>) = this shouldNot matchInOrder(assertions.toList(), allowGaps = true)

fun <T> Iterable<T>.shouldMatchEach(vararg assertions: (T) -> Unit) = toList().shouldMatchEach(assertions.toList())
fun <T> Array<T>.shouldMatchEach(vararg assertions: (T) -> Unit) = asList().shouldMatchEach(assertions.toList())
fun <T> List<T>.shouldMatchEach(vararg assertions: (T) -> Unit) = this.shouldMatchEach(assertions.toList())
infix fun <T> Iterable<T>.shouldMatchEach(assertions: List<(T) -> Unit>) = toList().shouldMatchEach(assertions)
fun <T> Iterable<T>.shouldMatchEach(expected: Iterable<T>, asserter: (T, T) -> Unit) = toList().shouldMatchEach(expected.toList(), asserter)
infix fun <T> Array<T>.shouldMatchEach(assertions: List<(T) -> Unit>) = asList().shouldMatchEach(assertions)
infix fun <T> List<T>.shouldMatchEach(assertions: List<(T) -> Unit>) = this should matchEach(assertions.toList())
fun <T> List<T>.shouldMatchEach(expected: List<T>, asserter: (T, T) -> Unit) = this should matchEach(expected, asserter)
fun <T> Iterable<T>.shouldNotMatchEach(vararg assertions: (T) -> Unit) = toList().shouldNotMatchEach(assertions.toList())
fun <T> Array<T>.shouldNotMatchEach(vararg assertions: (T) -> Unit) = asList().shouldNotMatchEach(assertions.toList())
fun <T> List<T>.shouldNotMatchEach(vararg assertions: (T) -> Unit) = this.shouldNotMatchEach(assertions.toList())
infix fun <T> Iterable<T>.shouldNotMatchEach(assertions: List<(T) -> Unit>) = toList().shouldNotMatchEach(assertions)
infix fun <T> Array<T>.shouldNotMatchEach(assertions: List<(T) -> Unit>) = asList().shouldNotMatchEach(assertions)
infix fun <T> List<T>.shouldNotMatchEach(assertions: List<(T) -> Unit>) = this shouldNot matchEach(assertions.toList())

fun <T> Iterable<T>.shouldExistInOrder(vararg ps: (T) -> Boolean) = toList().shouldExistInOrder(ps.toList())
fun <T> Array<T>.shouldExistInOrder(vararg ps: (T) -> Boolean) = asList().shouldExistInOrder(ps.toList())
fun <T> List<T>.shouldExistInOrder(vararg ps: (T) -> Boolean) = this.shouldExistInOrder(ps.toList())
infix fun <T> Iterable<T>.shouldExistInOrder(expected: List<(T) -> Boolean>) = toList().shouldExistInOrder(expected)
infix fun <T> Array<T>.shouldExistInOrder(expected: List<(T) -> Boolean>) = asList().shouldExistInOrder(expected)
infix fun <T> List<T>.shouldExistInOrder(expected: List<(T) -> Boolean>) = this should existInOrder(expected)
infix fun <T> Iterable<T>.shouldNotExistInOrder(expected: Iterable<(T) -> Boolean>) =
   toList().shouldNotExistInOrder(expected.toList())

infix fun <T> Array<T>.shouldNotExistInOrder(expected: Array<(T) -> Boolean>) =
   asList().shouldNotExistInOrder(expected.asList())

infix fun <T> Iterable<T>.shouldNotExistInOrder(expected: List<(T) -> Boolean>) =
   toList().shouldNotExistInOrder(expected)

infix fun <T> Array<T>.shouldNotExistInOrder(expected: List<(T) -> Boolean>) = asList().shouldNotExistInOrder(expected)
infix fun <T> List<T>.shouldNotExistInOrder(expected: List<(T) -> Boolean>) = this shouldNot existInOrder(expected)



fun <T> Iterable<T>.shouldContainAnyOf(vararg ts: T) = toList().shouldContainAnyOf(*ts)
fun <T> Array<T>.shouldContainAnyOf(vararg ts: T) = asList().shouldContainAnyOf(*ts)
fun <T> Collection<T>.shouldContainAnyOf(vararg ts: T) = this should containAnyOf(ts.asList())
fun <T> Iterable<T>.shouldNotContainAnyOf(vararg ts: T) = toList().shouldNotContainAnyOf(*ts)
fun <T> Array<T>.shouldNotContainAnyOf(vararg ts: T) = asList().shouldNotContainAnyOf(*ts)
fun <T> Collection<T>.shouldNotContainAnyOf(vararg ts: T) = this shouldNot containAnyOf(ts.asList())
infix fun <T> Iterable<T>.shouldContainAnyOf(ts: Collection<T>) = toList().shouldContainAnyOf(ts)
infix fun <T> Array<T>.shouldContainAnyOf(ts: Collection<T>) = asList().shouldContainAnyOf(ts)
infix fun <T> Collection<T>.shouldContainAnyOf(ts: Collection<T>) = this should containAnyOf(ts)
infix fun <T> Iterable<T>.shouldNotContainAnyOf(ts: Collection<T>) = toList().shouldNotContainAnyOf(ts)
infix fun <T> Array<T>.shouldNotContainAnyOf(ts: Collection<T>) = asList().shouldNotContainAnyOf(ts)
infix fun <T> Collection<T>.shouldNotContainAnyOf(ts: Collection<T>) = this shouldNot containAnyOf(ts)

fun <T> containAnyOf(ts: Collection<T>) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult {
      if (ts.isEmpty()) throwEmptyCollectionError()
      return MatcherResult(
         ts.any { it in value },
         { "Collection ${value.print().value} should contain any of ${ts.print().value}" },
         { "Collection ${value.print().value} should not contain any of ${ts.print().value}" }
      )
   }
}



internal fun throwEmptyCollectionError(): Nothing {
   throw AssertionError("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
}
