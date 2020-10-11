package io.kotest.matchers.collections

import io.kotest.assertions.show.show
import io.kotest.matchers.*



fun <T> Iterable<T>.shouldHaveElementAt(index: Int, element: T) = toList().shouldHaveElementAt(index, element)
fun <T> Array<T>.shouldHaveElementAt(index: Int, element: T) = asList().shouldHaveElementAt(index, element)
fun <T> List<T>.shouldHaveElementAt(index: Int, element: T) = this should haveElementAt(index, element)

fun <T> Iterable<T>.shouldNotHaveElementAt(index: Int, element: T) = toList().shouldNotHaveElementAt(index, element)
fun <T> Array<T>.shouldNotHaveElementAt(index: Int, element: T) = asList().shouldNotHaveElementAt(index, element)
fun <T> List<T>.shouldNotHaveElementAt(index: Int, element: T) = this shouldNot haveElementAt(index, element)


fun <T, L : List<T>> haveElementAt(index: Int, element: T) = object : Matcher<L> {
   override fun test(value: L) =
      MatcherResult(
         value[index] == element,
         { "Collection should contain ${element.show().value} at index $index" },
         { "Collection should not contain ${element.show().value} at index $index" }
      )
}




infix fun <T> Array<T>.shouldNotContainExactlyInAnyOrder(expected: Array<T>) =
   asList().shouldNotContainExactlyInAnyOrder(expected.asList())

infix fun <T, C : Collection<T>> C?.shouldNotContainExactlyInAnyOrder(expected: C) =
   this shouldNot containExactlyInAnyOrder(expected)

fun <T, C : Collection<T>> C?.shouldNotContainExactlyInAnyOrder(vararg expected: T) =
   this shouldNot containExactlyInAnyOrder(*expected)

infix fun <T> Array<T>.shouldContainExactlyInAnyOrder(expected: Array<T>) =
   asList().shouldContainExactlyInAnyOrder(expected.asList())

infix fun <T, C : Collection<T>> C?.shouldContainExactlyInAnyOrder(expected: C) =
   this should containExactlyInAnyOrder(expected)

fun <T, C : Collection<T>> C?.shouldContainExactlyInAnyOrder(vararg expected: T) =
   this should containExactlyInAnyOrder(*expected)

fun <T> containExactlyInAnyOrder(vararg expected: T): Matcher<Collection<T>?> =
   containExactlyInAnyOrder(expected.asList())

/** Assert that a collection contains exactly the given values and nothing else, in any order. */
fun <T, C : Collection<T>> containExactlyInAnyOrder(expected: C): Matcher<C?> = neverNullMatcher { value ->
   val valueGroupedCounts: Map<T, Int> = value.groupBy { it }.mapValues { it.value.size }
   val expectedGroupedCounts: Map<T, Int> = expected.groupBy { it }.mapValues { it.value.size }
   val passed = expectedGroupedCounts.size == valueGroupedCounts.size
      && expectedGroupedCounts.all { valueGroupedCounts[it.key] == it.value }

   MatcherResult(
      passed,
      "Collection should contain ${expected.show().value} in any order, but was ${value.show().value}",
      "Collection should not contain exactly ${expected.show().value} in any order"
   )
}


infix fun <T> Iterable<T>.shouldHaveSingleElement(t: T): Iterable<T> {
   toList().shouldHaveSingleElement(t)
   return this
}

infix fun <T> Array<T>.shouldHaveSingleElement(t: T): Array<T> {
   asList().shouldHaveSingleElement(t)
   return this
}

infix fun <T> Iterable<T>.shouldHaveSingleElement(p: (T) -> Boolean): Iterable<T> {
   toList().shouldHaveSingleElement(p)
   return this
}

infix fun <T> Array<T>.shouldHaveSingleElement(p: (T) -> Boolean) = asList().shouldHaveSingleElement(p)
infix fun <T> Collection<T>.shouldHaveSingleElement(t: T) = this should singleElement(t)
infix fun <T> Collection<T>.shouldHaveSingleElement(p: (T) -> Boolean) = this should singleElement(p)
infix fun <T> Iterable<T>.shouldNotHaveSingleElement(t: T) = toList().shouldNotHaveSingleElement(t)
infix fun <T> Array<T>.shouldNotHaveSingleElement(t: T) = asList().shouldNotHaveSingleElement(t)
infix fun <T> Collection<T>.shouldNotHaveSingleElement(t: T) = this shouldNot singleElement(t)


infix fun <T> Iterable<T>.shouldExist(p: (T) -> Boolean) = toList().shouldExist(p)
infix fun <T> Array<T>.shouldExist(p: (T) -> Boolean) = asList().shouldExist(p)
infix fun <T> Collection<T>.shouldExist(p: (T) -> Boolean) = this should exist(p)
fun <T> exist(p: (T) -> Boolean) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.any { p(it) },
      "Collection should contain an element that matches the predicate $p",
      "Collection should not contain an element that matches the predicate $p"
   )
}

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



fun <T> Iterable<T>.shouldContainAnyOf(vararg ts: T) = toList().shouldContainAnyOf(ts)
fun <T> Array<T>.shouldContainAnyOf(vararg ts: T) = asList().shouldContainAnyOf(ts)
fun <T> Collection<T>.shouldContainAnyOf(vararg ts: T) = this should containAnyOf(ts.asList())
fun <T> Iterable<T>.shouldNotContainAnyOf(vararg ts: T) = toList().shouldNotContainAnyOf(ts)
fun <T> Array<T>.shouldNotContainAnyOf(vararg ts: T) = asList().shouldNotContainAnyOf(ts)
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
         { "Collection should contain any of ${ts.joinToString(separator = ", ", limit = 10) { it.show().value }}" },
         { "Collection should not contain any of ${ts.joinToString(separator = ", ", limit = 10) { it.show().value }}" }
      )
   }
}



internal fun throwEmptyCollectionError(): Nothing {
   throw AssertionError("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
}

