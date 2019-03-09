package io.kotlintest.matchers.collections

import io.kotlintest.*
import io.kotlintest.matchers.*

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

infix fun <T> List<T>.shouldStartWith(slice: Collection<T>) = this should startWith(slice)
infix fun <T> List<T>.shouldNotStartWith(slice: Collection<T>) = this shouldNot startWith(slice)
fun <T> startWith(slice: Collection<T>) = object : Matcher<List<T>> {
  override fun test(value: List<T>) =
      Result(
          value.subList(0, slice.size) == slice,
          "List should start with ${stringRepr(slice)}",
          "List should not start with ${stringRepr(slice)}"
      )
}

infix fun <T> List<T>.shouldEndWith(slice: Collection<T>) = this should endWith(slice)
infix fun <T> List<T>.shouldNotEndWith(slice: Collection<T>) = this shouldNot endWith(slice)
fun <T> endWith(slice: Collection<T>) = object : Matcher<List<T>> {
  override fun test(value: List<T>) =
      Result(
          value.subList(value.size - slice.size, value.size) == slice,
          "List should end with ${stringRepr(slice)}",
          "List should not end with ${stringRepr(slice)}"
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

infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveUpperBound(t: T) = this should haveUpperBound(t)
fun <T : Comparable<T>, C : Collection<T>> haveUpperBound(t: T) = object : Matcher<C> {
  override fun test(value: C) = Result(
      value.all { it <= t },
      "Collection should have upper bound $t",
      "Collection should not have upper bound $t"
  )
}

infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveLowerBound(t: T) = this should haveLowerBound(t)
fun <T : Comparable<T>, C : Collection<T>> haveLowerBound(t: T) = object : Matcher<C> {
  override fun test(value: C) = Result(
      value.all { t <= it },
      "Collection should have lower bound $t",
      "Collection should not have lower bound $t"
  )
}

fun <T> Collection<T>.shouldBeUnique() = this should beUnique()
fun <T> Collection<T>.shouldNotBeUnique() = this shouldNot beUnique()
fun <T> beUnique() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.toSet().size == value.size,
      "Collection should be Unique",
      "Collection should contain at least one duplicate element"
  )
}

fun <T> Collection<T>.shouldContainDuplicates() = this should containDuplicates()
fun <T> Collection<T>.shouldNotContainDuplicates() = this shouldNot containDuplicates()
fun <T> containDuplicates() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.toSet().size < value.size,
      "Collection should contain duplicates",
      "Collection should not contain duplicates"
  )
}


fun <T> beSortedWith(comparator: Comparator<in T>): Matcher<List<T>> = sortedWith(comparator)
fun <T> beSortedWith(cmp: (T, T) -> Int): Matcher<List<T>> = sortedWith(cmp)
fun <T> sortedWith(comparator: Comparator<in T>): Matcher<List<T>> = sortedWith { a, b -> comparator.compare(a, b) }
fun <T> sortedWith(cmp: (T, T) -> Int): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): Result {
    val failure = value.withIndex().firstOrNull { (i, it) -> i != value.lastIndex && cmp(it, value[i+1]) > 0 }
    val snippet = value.joinToString(",", limit = 10)
    val elementMessage = when (failure) {
        null -> ""
        else -> ". Element ${failure.value} at index ${failure.index} shouldn't precede element ${value[failure.index+1]}"
    }
    return Result(
        failure == null,
        "List [$snippet] should be sorted$elementMessage",
        "List [$snippet] should not be sorted"
    )
  }
}

fun <T : Comparable<T>> List<T>.shouldBeSorted() = this should beSorted<T>()
fun <T : Comparable<T>> List<T>.shouldNotBeSorted() = this shouldNot beSorted<T>()
infix fun <T> List<T>.shouldBeSortedWith(comparator: Comparator<in T>) = this should beSortedWith(comparator)
infix fun <T> List<T>.shouldNotBeSortedWith(comparator: Comparator<in T>) = this shouldNot beSortedWith(comparator)
infix fun <T> List<T>.shouldBeSortedWith(cmp: (T, T) -> Int) = this should beSortedWith(cmp)
infix fun <T> List<T>.shouldNotBeSortedWith(cmp: (T, T) -> Int) = this shouldNot beSortedWith(cmp)

infix fun <T> Collection<T>.shouldHaveSingleElement(t: T) = this should singleElement(t)
infix fun <T> Collection<T>.shouldNotHaveSingleElement(t: T) = this shouldNot singleElement(t)
infix fun <T> Collection<T>.shouldHaveSize(size: Int) = this should haveSize(size)
infix fun <T> Collection<T>.shouldNotHaveSize(size: Int) = this shouldNot haveSize(size)

infix fun <T, U> Collection<T>.shouldBeLargerThan(other: Collection<U>) = this should beLargerThan(other)
fun <T, U> beLargerThan(other: Collection<U>) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.size > other.size,
      "Collection of size ${value.size} should be larger than collection of size ${other.size}",
      "Collection of size ${value.size} should not be larger than collection of size ${other.size}"
  )
}

infix fun <T, U> Collection<T>.shouldBeSmallerThan(other: Collection<U>) = this should beSmallerThan(other)
fun <T, U> beSmallerThan(other: Collection<U>) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.size < other.size,
      "Collection of size ${value.size} should be smaller than collection of size ${other.size}",
      "Collection of size ${value.size} should not be smaller than collection of size ${other.size}"
  )
}

infix fun <T, U> Collection<T>.shouldBeSameSizeAs(other: Collection<U>) = this should beSameSizeAs(other)
fun <T, U> beSameSizeAs(other: Collection<U>) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.size == other.size,
      "Collection of size ${value.size} should be the same size as collection of size ${other.size}",
      "Collection of size ${value.size} should not be the same size as collection of size ${other.size}"
  )
}

infix fun <T> Collection<T>.shouldHaveAtLeastSize(n: Int) = this shouldHave atLeastSize(n)
fun <T> atLeastSize(n: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.size >= n,
      "Collection should contain at least $n elements",
      "Collection should contain less than $n elements"
  )
}

infix fun <T> Collection<T>.shouldHaveAtMostSize(n: Int) = this shouldHave atMostSize(n)
fun <T> atMostSize(n: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.size <= n,
      "Collection should contain at most $n elements",
      "Collection should contain more than $n elements"
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

fun <T> List<T>.shouldContainInOrder(vararg ts: T) = this.shouldContainInOrder(ts.toList())
infix fun <T> List<T>.shouldContainInOrder(expected: List<T>) = this should containsInOrder(expected)
infix fun <T> List<T>.shouldNotContainInOrder(expected: List<T>) = this shouldNot containsInOrder(expected)

fun <T> Collection<T>.shouldBeEmpty() = this should beEmpty()
fun <T> Collection<T>.shouldNotBeEmpty() = this shouldNot beEmpty()

fun <T> Collection<T>.shouldContainAll(vararg ts: T) = this should containAll(*ts)
fun <T> Collection<T>.shouldNotContainAll(vararg ts: T) = this shouldNot containAll(*ts)
infix fun <T> Collection<T>.shouldContainAll(ts: Collection<T>) = this should containAll(ts)
infix fun <T> Collection<T>.shouldNotContainAll(ts: Collection<T>) = this shouldNot containAll(ts)


/**
 * Verifies that this instance is in [collection]
 *
 * Assertion to check that this instance is in [collection]. This assertion checks by reference, and not by value,
 * therefore the exact instance must be in [collection], or this will fail.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldNotBeOneOf]
 * @see [beOneOf]
 */
infix fun <T> T.shouldBeOneOf(collection: Collection<T>) = this should beOneOf(collection)

/**
 * Verifies that this instance is NOT in [collection]
 *
 * Assertion to check that this instance is not in [collection]. This assertion checks by reference, and not by value,
 * therefore the exact instance must not be in [collection], or this will fail.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldBeOneOf]
 * @see [beOneOf]
 */
infix fun <T> T.shouldNotBeOneOf(collection: Collection<T>) = this shouldNot beOneOf(collection)

/**
 * Verifies that this instance is any of [any]
 *
 * Assertion to check that this instance is any of [any]. This assertion checks by reference, and not by value,
 * therefore the exact instance must be in [any], or this will fail.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldNotBeOneOf]
 * @see [beOneOf]
 */
fun <T> T.shouldBeOneOf(vararg any: T) = this should beOneOf(any.toList())

/**
 * Verifies that this instance is NOT any of [any]
 *
 * Assertion to check that this instance is not any of [any]. This assertion checks by reference, and not by value,
 * therefore the exact instance must not be in [any], or this will fail.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldNotBeOneOf]
 * @see [beOneOf]
 */
fun <T> T.shouldNotBeOneOf(vararg any: T) = this shouldNot beOneOf(any.toList())

/**
 * Mather that verifies that this instance is in [collection]
 *
 * Assertion to check that this instance is in [collection]. This matcher checks by reference, and not by value,
 * therefore the exact instance must be in [collection], or this will fail.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldBeOneOf]
 * @see [shouldNotBeOneOf]
 */
fun <T> beOneOf(collection: Collection<T>) = object : Matcher<T> {
  override fun test(value: T): Result {
    if(collection.isEmpty()) throwEmptyCollectionError()
    
    val match = collection.any { it === value }
    return Result(match,
            "Collection should contain the instance of value, but doesn't.",
            "Collection should not contain the instance of value, but does.")
  }
}

private fun throwEmptyCollectionError(): Nothing {
  throw AssertionError("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
}

