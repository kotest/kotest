package io.kotlintest.matchers.collections

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.neverNullMatcher
import io.kotlintest.should
import io.kotlintest.shouldNot
import io.kotlintest.stringRepr

fun <T> Collection<T>.shouldContainOnlyNulls() = this should containOnlyNulls()
fun <T> Collection<T>.shouldNotContainOnlyNulls() = this shouldNot containOnlyNulls()
fun <T> containOnlyNulls() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
          Result(
                  value.all { it == null },
                  "Collection should contain only nulls",
                  "Collection should not contain only nulls"
          )
}

fun <T> Collection<T>.shouldContainNoNulls() = this should containNoNulls()
fun <T> Collection<T>.shouldNotContainNoNulls() = this shouldNot containNoNulls()
fun <T> containNoNulls() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
          Result(
                  value.all { it != null },
                  "Collection should not contain nulls",
                  "Collection should have at least one null"
          )
}


fun <T> Collection<T>.shouldContainNull() = this should containNull()
fun <T> Collection<T>.shouldNotContainNull() = this shouldNot containNull()
fun <T> containNull() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
          Result(
                  value.any { it == null },
                  "Collection should contain at least one null",
                  "Collection should not contain any nulls"
          )
}

infix fun <T, C : Collection<T>> C.shouldContain(t: T) = this should contain(t)
infix fun <T, C : Collection<T>> C.shouldNotContain(t: T) = this shouldNot contain(t)
fun <T, C : Collection<T>> contain(t: T) = object : Matcher<C> {
  override fun test(value: C) = Result(
          value.contains(t),
          "Collection should contain element ${stringRepr(t)}",
          "Collection should not contain element ${stringRepr(t)}"
  )
}


infix fun <T, C : Collection<T>> C?.shouldNotContainExactly(expected: C) = this shouldNot containExactly(expected)
fun <T, C : Collection<T>> C?.shouldNotContainExactly(vararg expected: T) = this shouldNot containExactly(*expected)
infix fun <T, C : Collection<T>> C?.shouldContainExactly(expected: C) = this should containExactly(expected)
fun <T, C : Collection<T>> C?.shouldContainExactly(vararg expected: T) = this should containExactly(*expected)
fun <T> containExactly(vararg expected: T): Matcher<Collection<T>?> = containExactly(expected.asList())
/** Assert that a collection contains exactly the given values and nothing else, in order. */
fun <T, C : Collection<T>> containExactly(expected: C): Matcher<C?> = neverNullMatcher { value ->
  val passed = value.size == expected.size && value.zip(expected) { a, b -> a == b }.all { it }
  Result(
          passed,
          "Collection should be exactly ${stringRepr(expected)} but was ${stringRepr(value)}",
          "Collection should not be exactly ${stringRepr(expected)}"
  )
}


infix fun <T, C : Collection<T>> C?.shouldNotContainExactlyInAnyOrder(expected: C) = this shouldNot containExactlyInAnyOrder(expected)
fun <T, C : Collection<T>> C?.shouldNotContainExactlyInAnyOrder(vararg expected: T) = this shouldNot containExactlyInAnyOrder(*expected)
infix fun <T, C : Collection<T>> C?.shouldContainExactlyInAnyOrder(expected: C) = this should containExactlyInAnyOrder(expected)
fun <T, C : Collection<T>> C?.shouldContainExactlyInAnyOrder(vararg expected: T) = this should containExactlyInAnyOrder(*expected)
fun <T> containExactlyInAnyOrder(vararg expected: T): Matcher<Collection<T>?> = containExactlyInAnyOrder(expected.asList())
/** Assert that a collection contains exactly the given values and nothing else, in any order. */
fun <T, C : Collection<T>> containExactlyInAnyOrder(expected: C): Matcher<C?> = neverNullMatcher { value ->
  val passed = value.size == expected.size && expected.all { value.contains(it) }
  Result(
          passed,
          "Collection should contain ${stringRepr(expected)} in any order, but was ${stringRepr(value)}",
          "Collection should not contain exactly ${stringRepr(expected)} in any order"
  )
}

fun <T : Comparable<T>> List<T>.shouldContainInOrder(vararg ts: T) = this.shouldContainInOrder(ts.toList())
infix fun <T : Comparable<T>> List<T>.shouldContainInOrder(expected: List<T>) = this should containsInOrder(expected)
infix fun <T : Comparable<T>> List<T>.shouldNotContainInOrder(expected: List<T>) = this shouldNot containsInOrder(expected)

fun <T> containsInOrder(vararg ts: T): Matcher<Collection<T>?> = containsInOrder(ts.asList())
/** Assert that a collection contains a given subsequence, possibly with values in between. */
fun <T> containsInOrder(subsequence: List<T>): Matcher<Collection<T>?> = neverNullMatcher { actual ->
  assert(subsequence.isNotEmpty(), { "expected values must not be empty" })

  var subsequenceIndex = 0
  val actualIterator = actual.iterator()

  while (actualIterator.hasNext() && subsequenceIndex < subsequence.size) {
    if (actualIterator.next() == subsequence[subsequenceIndex]) subsequenceIndex += 1
  }

  Result(
          subsequenceIndex == subsequence.size,
          "${stringRepr(actual)} did not contain the elements ${stringRepr(subsequence)} in order",
          "${stringRepr(actual)} should not contain the elements ${stringRepr(subsequence)} in order"
  )
}

fun <T> Collection<T>.shouldContainAll(vararg ts: T) = this should containAll(*ts)
fun <T> Collection<T>.shouldNotContainAll(vararg ts: T) = this shouldNot containAll(*ts)
infix fun <T> Collection<T>.shouldContainAll(ts: Collection<T>) = this should containAll(ts)
infix fun <T> Collection<T>.shouldNotContainAll(ts: Collection<T>) = this shouldNot containAll(ts)

fun <T> containAll(vararg ts: T) = containAll(ts.asList())
fun <T> containAll(ts: Collection<T>): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
          ts.all { value.contains(it) },
          "Collection should contain all of ${ts.joinToString(", ", limit = 10) { stringRepr(it) }}",
          "Collection should not contain all of ${ts.joinToString(", ", limit = 10) { stringRepr(it) }}"
  )
}

fun <T> List<T>.shouldHaveElementAt(index: Int, element: T) = this should haveElementAt(index, element)
fun <T> List<T>.shouldNotHaveElementAt(index: Int, element: T) = this shouldNot haveElementAt(index, element)

fun <T, L : List<T>> haveElementAt(index: Int, element: T) = object : Matcher<L> {
  override fun test(value: L) =
          Result(
                  value[index] == element,
                  "Collection should contain ${stringRepr(element)} at index $index",
                  "Collection should not contain ${stringRepr(element)} at index $index"
          )
}

infix fun <T> Collection<T>.shouldHaveSingleElement(t: T) = this should singleElement(t)
infix fun <T> Collection<T>.shouldNotHaveSingleElement(t: T) = this shouldNot singleElement(t)

fun <T> singleElement(t: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
          value.size == 1 && value.first() == t,
          "Collection should be a single element of $t but has ${value.size} elements",
          "Collection should not be a single element of $t"
  )
}

infix fun <T> Collection<T>.shouldExist(p: (T) -> Boolean) = this should exist(p)
fun <T> exist(p: (T) -> Boolean) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
          value.any { p(it) },
          "Collection should contain an element that matches the predicate $p",
          "Collection should not contain an element that matches the predicate $p"
  )
}