package io.kotest.matchers.collections

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.assertions.stringRepr
import io.kotest.neverNullMatcher
import io.kotest.should
import io.kotest.shouldHave
import io.kotest.shouldNot
import kotlin.jvm.JvmName

fun <T> Array<T>.shouldContainOnlyNulls() = asList().shouldContainOnlyNulls()
fun <T> Collection<T>.shouldContainOnlyNulls() = this should containOnlyNulls()
fun <T> Array<T>.shouldNotContainOnlyNulls() = asList().shouldNotContainOnlyNulls()
fun <T> Collection<T>.shouldNotContainOnlyNulls() = this shouldNot containOnlyNulls()
fun <T> containOnlyNulls() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
    MatcherResult(
      value.all { it == null },
      "Collection should contain only nulls",
      "Collection should not contain only nulls"
    )
}

fun <T> Array<T>.shouldContainNull() = asList().shouldContainNull()
fun <T> Collection<T>.shouldContainNull() = this should containNull()
fun <T> Array<T>.shouldNotContainNull() = asList().shouldNotContainNull()
fun <T> Collection<T>.shouldNotContainNull() = this shouldNot containNull()
fun <T> containNull() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
    MatcherResult(
      value.any { it == null },
      "Collection should contain at least one null",
      "Collection should not contain any nulls"
    )
}

infix fun <T> Array<T>.shouldStartWith(slice: Collection<T>) = asList().shouldStartWith(slice)
infix fun <T> Array<T>.shouldStartWith(slice: Array<T>) = asList().shouldStartWith(slice.asList())
infix fun <T> List<T>.shouldStartWith(slice: Collection<T>) = this should startWith(slice)
infix fun <T> Array<T>.shouldNotStartWith(slice: Collection<T>) = asList().shouldNotStartWith(slice)
infix fun <T> Array<T>.shouldNotStartWith(slice: Array<T>) = asList().shouldNotStartWith(slice.asList())
infix fun <T> List<T>.shouldNotStartWith(slice: Collection<T>) = this shouldNot startWith(slice)
fun <T> startWith(slice: Collection<T>) = object : Matcher<List<T>> {
  override fun test(value: List<T>) =
    MatcherResult(
      value.subList(0, slice.size) == slice,
      { "List should start with ${stringRepr(slice)}" },
      { "List should not start with ${stringRepr(slice)}" }
    )
}

infix fun <T> Array<T>.shouldEndWith(slice: Collection<T>) = asList().shouldEndWith(slice)
infix fun <T> Array<T>.shouldEndWith(slice: Array<T>) = asList().shouldEndWith(slice.asList())
infix fun <T> List<T>.shouldEndWith(slice: Collection<T>) = this should endWith(slice)
infix fun <T> Array<T>.shouldNotEndWith(slice: Collection<T>) = asList().shouldNotEndWith(slice)
infix fun <T> Array<T>.shouldNotEndWith(slice: Array<T>) = asList().shouldNotEndWith(slice.asList())
infix fun <T> List<T>.shouldNotEndWith(slice: Collection<T>) = this shouldNot endWith(slice)
fun <T> endWith(slice: Collection<T>) = object : Matcher<List<T>> {
  override fun test(value: List<T>) =
    MatcherResult(
      value.subList(value.size - slice.size, value.size) == slice,
      { "List should end with ${stringRepr(slice)}" },
      { "List should not end with ${stringRepr(slice)}" }
    )
}

fun <T> Array<T>.shouldHaveElementAt(index: Int, element: T) = asList().shouldHaveElementAt(index, element)
fun <T> List<T>.shouldHaveElementAt(index: Int, element: T) = this should haveElementAt(index, element)

fun <T> Array<T>.shouldNotHaveElementAt(index: Int, element: T) = asList().shouldNotHaveElementAt(index, element)
fun <T> List<T>.shouldNotHaveElementAt(index: Int, element: T) = this shouldNot haveElementAt(index, element)


fun <T, L : List<T>> haveElementAt(index: Int, element: T) = object : Matcher<L> {
  override fun test(value: L) =
    MatcherResult(
      value[index] == element,
      { "Collection should contain ${stringRepr(element)} at index $index" },
      { "Collection should not contain ${stringRepr(element)} at index $index" }
    )
}

fun <T> Array<T>.shouldContainNoNulls() = asList().shouldContainNoNulls()
fun <T> Collection<T>.shouldContainNoNulls() = this should containNoNulls()
fun <T> Array<T>.shouldNotContainNoNulls() = asList().shouldNotContainNoNulls()
fun <T> Collection<T>.shouldNotContainNoNulls() = this shouldNot containNoNulls()
fun <T> containNoNulls() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
    MatcherResult(
      value.all { it != null },
      { "Collection should not contain nulls" },
      { "Collection should have at least one null" }
    )
}

infix fun <T, C : Collection<T>> C.shouldContain(t: T) = this should contain(t)
infix fun <T, C : Collection<T>> C.shouldNotContain(t: T) = this shouldNot contain(t)
fun <T, C : Collection<T>> contain(t: T) = object : Matcher<C> {
  override fun test(value: C) = MatcherResult(
    value.contains(t),
    { "Collection should contain element ${stringRepr(t)}" },
    { "Collection should not contain element ${stringRepr(t)}" }
  )
}

infix fun <T> Array<T>.shouldNotContainExactly(expected: Array<T>) = asList().shouldNotContainExactly(expected.asList())
infix fun <T, C : Collection<T>> C?.shouldNotContainExactly(expected: C) = this shouldNot containExactly(expected)

fun <T, C : Collection<T>> C?.shouldNotContainExactly(vararg expected: T) = this shouldNot containExactly(*expected)
infix fun <T> Array<T>.shouldContainExactly(expected: Array<T>) = asList().shouldContainExactly(expected)
infix fun <T, C : Collection<T>> C?.shouldContainExactly(expected: C) = this should containExactly(expected)

fun <T, C : Collection<T>> C?.shouldContainExactly(vararg expected: T) = this should containExactly(*expected)

fun <T> containExactly(vararg expected: T): Matcher<Collection<T>?> = containExactly(
  expected.asList())
/** Assert that a collection contains exactly the given values and nothing else, in order. */
fun <T, C : Collection<T>> containExactly(expected: C): Matcher<C?> = neverNullMatcher { value ->
  val passed = value.size == expected.size && value.zip(expected) { a, b -> a == b }.all { it }
  MatcherResult(
    passed,
    { "Collection should be exactly ${stringRepr(expected)} but was ${stringRepr(value)}" },
    { "Collection should not be exactly ${stringRepr(expected)}" }
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
   val passed = value.size == expected.size && expected.all { value.contains(it) }
   MatcherResult(
      passed,
      "Collection should contain ${stringRepr(expected)} in any order, but was ${stringRepr(value)}",
      "Collection should not contain exactly ${stringRepr(expected)} in any order"
   )
}

infix fun <T : Comparable<T>> Array<T>.shouldHaveUpperBound(t: T) = asList().shouldHaveUpperBound(t)
infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveUpperBound(t: T) = this should haveUpperBound(t)

fun <T : Comparable<T>, C : Collection<T>> haveUpperBound(t: T) = object : Matcher<C> {
   override fun test(value: C) = MatcherResult(
      value.all { it <= t },
      "Collection should have upper bound $t",
      "Collection should not have upper bound $t"
   )
}

infix fun <T : Comparable<T>> Array<T>.shouldHaveLowerBound(t: T) = asList().shouldHaveLowerBound(t)
infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveLowerBound(t: T) = this should haveLowerBound(t)
fun <T : Comparable<T>, C : Collection<T>> haveLowerBound(t: T) = object : Matcher<C> {
  override fun test(value: C) = MatcherResult(
    value.all { t <= it },
    "Collection should have lower bound $t",
    "Collection should not have lower bound $t"
  )
}

fun <T> Array<T>.shouldBeUnique() = asList().shouldBeUnique()
fun <T> Collection<T>.shouldBeUnique() = this should beUnique()
fun <T> Array<T>.shouldNotBeUnique() = asList().shouldNotBeUnique()
fun <T> Collection<T>.shouldNotBeUnique() = this shouldNot beUnique()
fun <T> beUnique() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = MatcherResult(
    value.toSet().size == value.size,
    "Collection should be Unique",
    "Collection should contain at least one duplicate element"
  )
}

fun <T> Array<T>.shouldContainDuplicates() = asList().shouldContainDuplicates()
fun <T> Collection<T>.shouldContainDuplicates() = this should containDuplicates()
fun <T> Array<T>.shouldNotContainDuplicates() = asList().shouldNotContainDuplicates()
fun <T> Collection<T>.shouldNotContainDuplicates() = this shouldNot containDuplicates()
fun <T> containDuplicates() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = MatcherResult(
    value.toSet().size < value.size,
    "Collection should contain duplicates",
    "Collection should not contain duplicates"
  )
}


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
      "List [$snippet] should be sorted$elementMessage",
      "List [$snippet] should not be sorted"
    )
  }
}

fun <T : Comparable<T>> List<T>.shouldBeSorted() = this should beSorted<T>()
fun <T : Comparable<T>> Array<T>.shouldBeSorted() = asList().shouldBeSorted()
fun <T : Comparable<T>> Array<T>.shouldNotBeSorted() = asList().shouldNotBeSorted()
fun <T : Comparable<T>> List<T>.shouldNotBeSorted() = this shouldNot beSorted<T>()
infix fun <T> Array<T>.shouldBeSortedWith(comparator: Comparator<in T>) = asList().shouldBeSortedWith(comparator)
infix fun <T> List<T>.shouldBeSortedWith(comparator: Comparator<in T>) = this should beSortedWith(comparator)
infix fun <T> Array<T>.shouldNotBeSortedWith(comparator: Comparator<in T>) = asList().shouldNotBeSortedWith(comparator)
infix fun <T> List<T>.shouldNotBeSortedWith(comparator: Comparator<in T>) = this shouldNot beSortedWith(comparator)
infix fun <T> Array<T>.shouldBeSortedWith(cmp: (T, T) -> Int) = asList().shouldBeSortedWith(cmp)
infix fun <T> List<T>.shouldBeSortedWith(cmp: (T, T) -> Int) = this should beSortedWith(cmp)
infix fun <T> Array<T>.shouldNotBeSortedWith(cmp: (T, T) -> Int) = asList().shouldNotBeSortedWith(cmp)
infix fun <T> List<T>.shouldNotBeSortedWith(cmp: (T, T) -> Int) = this shouldNot beSortedWith(cmp)

fun <T : Comparable<T>> List<T>.shouldBeMonotonicallyIncreasing() = this should beMonotonicallyIncreasing<T>()
fun <T : Comparable<T>> Array<T>.shouldBeMonotonicallyIncreasing() = asList().shouldBeMonotonicallyIncreasing()
fun <T : Comparable<T>> List<T>.shouldNotBeMonotonicallyIncreasing() = this shouldNot beMonotonicallyIncreasing<T>()
fun <T : Comparable<T>> Array<T>.shouldNotBeMonotonicallyIncreasing() = asList().shouldNotBeMonotonicallyIncreasing()
fun <T> List<T>.shouldBeMonotonicallyIncreasingWith(comparator: Comparator<in T>) = this should beMonotonicallyIncreasingWith(comparator)
fun <T> Array<T>.shouldBeMonotonicallyIncreasingWith(comparator: Comparator<in T>) = asList().shouldBeMonotonicallyIncreasingWith(comparator)
fun <T> List<T>.shouldNotBeMonotonicallyIncreasingWith(comparator: Comparator<in T>) = this shouldNot beMonotonicallyIncreasingWith(comparator)
fun <T> Array<T>.shouldNotBeMonotonicallyIncreasingWith(comparator: Comparator<in T>) = asList().shouldNotBeMonotonicallyIncreasingWith(comparator)


fun <T : Comparable<T>> List<T>.shouldBeMonotonicallyDecreasing() = this should beMonotonicallyDecreasing<T>()
fun <T : Comparable<T>> Array<T>.shouldBeMonotonicallyDecreasing() = asList().shouldBeMonotonicallyDecreasing()
fun <T : Comparable<T>> List<T>.shouldNotBeMonotonicallyDecreasing() = this shouldNot beMonotonicallyDecreasing<T>()
fun <T : Comparable<T>> Array<T>.shouldNotBeMonotonicallyDecreasing() = asList().shouldNotBeMonotonicallyDecreasing()
fun <T> List<T>.shouldBeMonotonicallyDecreasingWith(comparator: Comparator<in T>) = this should beMonotonicallyDecreasingWith(comparator)
fun <T> Array<T>.shouldBeMonotonicallyDecreasingWith(comparator: Comparator<in T>) = asList().shouldBeMonotonicallyDecreasingWith(comparator)
fun <T> List<T>.shouldNotBeMonotonicallyDecreasingWith(comparator: Comparator<in T>) = this shouldNot beMonotonicallyDecreasingWith(comparator)
fun <T> Array<T>.shouldNotBeMonotonicallyDecreasingWith(comparator: Comparator<in T>) = asList().shouldNotBeMonotonicallyDecreasingWith(comparator)

fun <T : Comparable<T>> List<T>.shouldBeStrictlyIncreasing() = this should beStrictlyIncreasing<T>()
fun <T : Comparable<T>> Array<T>.shouldBeStrictlyIncreasing() = asList().shouldBeStrictlyIncreasing()
fun <T : Comparable<T>> List<T>.shouldNotBeStrictlyIncreasing() = this shouldNot beStrictlyIncreasing<T>()
fun <T : Comparable<T>> Array<T>.shouldNotBeStrictlyIncreasing() = asList().shouldNotBeStrictlyIncreasing()
fun <T> List<T>.shouldBeStrictlyIncreasingWith(comparator: Comparator<in T>) = this should beStrictlyIncreasingWith(comparator)
fun <T> Array<T>.shouldBeStrictlyIncreasingWith(comparator: Comparator<in T>) = asList().shouldBeStrictlyIncreasingWith(comparator)
fun <T> List<T>.shouldNotBeStrictlyIncreasingWith(comparator: Comparator<in T>) = this shouldNot beStrictlyIncreasingWith(comparator)
fun <T> Array<T>.shouldNotBeStrictlyIncreasingWith(comparator: Comparator<in T>) = asList().shouldNotBeStrictlyIncreasingWith(comparator)

fun <T : Comparable<T>> List<T>.shouldBeStrictlyDecreasing() = this should beStrictlyDecreasing<T>()
fun <T : Comparable<T>> Array<T>.shouldBeStrictlyDecreasing() = asList().shouldBeStrictlyDecreasing()
fun <T : Comparable<T>> List<T>.shouldNotBeStrictlyDecreasing() = this shouldNot beStrictlyDecreasing<T>()
fun <T : Comparable<T>> Array<T>.shouldNotBeStrictlyDecreasing() = asList().shouldNotBeStrictlyDecreasing()
fun <T> List<T>.shouldBeStrictlyDecreasingWith(comparator: Comparator<in T>) = this should beStrictlyDecreasingWith(comparator)
fun <T> Array<T>.shouldBeStrictlyDecreasingWith(comparator: Comparator<in T>) = asList().shouldBeStrictlyDecreasingWith(comparator)
fun <T> List<T>.shouldNotBeStrictlyDecreasingWith(comparator: Comparator<in T>) = this shouldNot beStrictlyDecreasingWith(comparator)
fun <T> Array<T>.shouldNotBeStrictlyDecreasingWith(comparator: Comparator<in T>) = asList().shouldNotBeStrictlyDecreasingWith(comparator)

infix fun <T> Array<T>.shouldHaveSingleElement(t: T) = asList().shouldHaveSingleElement(t)
infix fun <T> Collection<T>.shouldHaveSingleElement(t: T) = this should singleElement(t)
infix fun <T> Array<T>.shouldNotHaveSingleElement(t: T) = asList().shouldNotHaveSingleElement(t)
infix fun <T> Collection<T>.shouldNotHaveSingleElement(t: T) = this shouldNot singleElement(t)
infix fun <T> Array<T>.shouldHaveSize(size: Int) = asList().shouldHaveSize(size)
infix fun <T> Collection<T>.shouldHaveSize(size: Int) = this should haveSize(size)
infix fun <T> Array<T>.shouldNotHaveSize(size: Int) = asList().shouldNotHaveSize(size)
infix fun <T> Collection<T>.shouldNotHaveSize(size: Int) = this shouldNot haveSize(size)

/**
 * Verifies this collection contains only one element
 *
 * This assertion is an alias to `collection shouldHaveSize 1`. This will pass if the collection have exactly one element
 * (definition of a Singleton Collection)
 *
 * ```
 * listOf(1).shouldBeSingleton()    // Assertion passes
 * listOf(1, 2).shouldBeSingleton() // Assertion fails
 * ```
 *
 * @see [shouldHaveSize]
 * @see [shouldNotBeSingleton]
 * @see [shouldHaveSingleElement]
 */
fun <T> Collection<T>.shouldBeSingleton() = this shouldHaveSize 1

fun <T> Array<T>.shouldBeSingleton() = asList().shouldBeSingleton()

/**
 * Verifies this collection doesn't contain only one element
 *
 * This assertion is an alias to `collection shouldNotHaveSize 1`. This will pass if the collection doesn't have exactly one element
 * (definition of a Singleton Collection)
 *
 * ```
 * listOf(1, 2).shouldNotBeSingleton()    // Assertion passes
 * listOf<Int>().shouldNotBeSingleton()   // Assertion passes
 * listOf(1).shouldNotBeSingleton()       // Assertion fails
 * ```
 *
 * @see [shouldNotHaveSize]
 * @see [shouldBeSingleton]
 * @see [shouldNotHaveSingleElement]
 */
fun <T> Collection<T>.shouldNotBeSingleton() = this shouldNotHaveSize 1

fun <T> Array<T>.shouldNotBeSingleton() = asList().shouldNotBeSingleton()

infix fun <T, U> Array<T>.shouldBeLargerThan(other: Collection<U>) = asList().shouldBeLargerThan(other)
infix fun <T, U> Array<T>.shouldBeLargerThan(other: Array<U>) = asList().shouldBeLargerThan(other.asList())
infix fun <T, U> Collection<T>.shouldBeLargerThan(other: Collection<U>) = this should beLargerThan(other)
fun <T, U> beLargerThan(other: Collection<U>) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = MatcherResult(
    value.size > other.size,
    "Collection of size ${value.size} should be larger than collection of size ${other.size}",
    "Collection of size ${value.size} should not be larger than collection of size ${other.size}"
  )
}

infix fun <T, U> Array<T>.shouldBeSmallerThan(other: Collection<U>) = asList().shouldBeSmallerThan(other)
infix fun <T, U> Array<T>.shouldBeSmallerThan(other: Array<U>) = asList().shouldBeSmallerThan(other.asList())
infix fun <T, U> Collection<T>.shouldBeSmallerThan(other: Collection<U>) = this should beSmallerThan(other)
fun <T, U> beSmallerThan(other: Collection<U>) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = MatcherResult(
    value.size < other.size,
    "Collection of size ${value.size} should be smaller than collection of size ${other.size}",
    "Collection of size ${value.size} should not be smaller than collection of size ${other.size}"
  )
}

infix fun <T, U> Array<T>.shouldBeSameSizeAs(other: Collection<U>) = asList().shouldBeSameSizeAs(other)
infix fun <T, U> Array<T>.shouldBeSameSizeAs(other: Array<U>) = asList().shouldBeSameSizeAs(other.asList())
infix fun <T, U> Collection<T>.shouldBeSameSizeAs(other: Collection<U>) = this should beSameSizeAs(other)
fun <T, U> beSameSizeAs(other: Collection<U>) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = MatcherResult(
    value.size == other.size,
    "Collection of size ${value.size} should be the same size as collection of size ${other.size}",
    "Collection of size ${value.size} should not be the same size as collection of size ${other.size}"
  )
}

infix fun <T> Array<T>.shouldHaveAtLeastSize(n: Int) = asList().shouldHaveAtLeastSize(n)
infix fun <T> Collection<T>.shouldHaveAtLeastSize(n: Int) = this shouldHave atLeastSize(n)
fun <T> atLeastSize(n: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = MatcherResult(
    value.size >= n,
    "Collection should contain at least $n elements",
    "Collection should contain less than $n elements"
  )
}

infix fun <T> Array<T>.shouldHaveAtMostSize(n: Int) = asList().shouldHaveAtMostSize(n)
infix fun <T> Collection<T>.shouldHaveAtMostSize(n: Int) = this shouldHave atMostSize(n)
fun <T> atMostSize(n: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = MatcherResult(
    value.size <= n,
    "Collection should contain at most $n elements",
    "Collection should contain more than $n elements"
  )
}

infix fun <T> Array<T>.shouldExist(p: (T) -> Boolean) = asList().shouldExist(p)
infix fun <T> Collection<T>.shouldExist(p: (T) -> Boolean) = this should exist(p)
fun <T> exist(p: (T) -> Boolean) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = MatcherResult(
    value.any { p(it) },
    "Collection should contain an element that matches the predicate $p",
    "Collection should not contain an element that matches the predicate $p"
  )
}

fun <T> Array<T>.shouldContainInOrder(vararg ts: T) = asList().shouldContainInOrder(ts)
fun <T> List<T>.shouldContainInOrder(vararg ts: T) = this.shouldContainInOrder(ts.toList())
infix fun <T> Array<T>.shouldContainInOrder(expected: List<T>) = asList().shouldContainInOrder(expected)
infix fun <T> List<T>.shouldContainInOrder(expected: List<T>) = this should containsInOrder(expected)
infix fun <T> Array<T>.shouldNotContainInOrder(expected: Array<T>) = asList().shouldNotContainInOrder(expected.asList())
infix fun <T> Array<T>.shouldNotContainInOrder(expected: List<T>) = asList().shouldNotContainInOrder(expected)
infix fun <T> List<T>.shouldNotContainInOrder(expected: List<T>) = this shouldNot containsInOrder(expected)

fun <T> Array<T>.shouldBeEmpty() = asList().shouldBeEmpty()
fun <T> Collection<T>.shouldBeEmpty() = this should beEmpty()
fun <T> Array<T>.shouldNotBeEmpty() = asList().shouldNotBeEmpty()
fun <T> Collection<T>.shouldNotBeEmpty() = this shouldNot beEmpty()

fun <T> Array<T>.shouldContainAll(vararg ts: T) = asList().shouldContainAll(ts)
fun <T> Collection<T>.shouldContainAll(vararg ts: T) = this should containAll(*ts)
fun <T> Array<T>.shouldNotContainAll(vararg ts: T) = asList().shouldNotContainAll(ts)
fun <T> Collection<T>.shouldNotContainAll(vararg ts: T) = this shouldNot containAll(*ts)
infix fun <T> Array<T>.shouldContainAll(ts: Collection<T>) = asList().shouldContainAll(ts)
infix fun <T> Collection<T>.shouldContainAll(ts: Collection<T>) = this should containAll(ts)
infix fun <T> Array<T>.shouldNotContainAll(ts: Collection<T>) = asList().shouldNotContainAll(ts)
infix fun <T> Collection<T>.shouldNotContainAll(ts: Collection<T>) = this shouldNot containAll(ts)

fun <T> Array<T>.shouldContainAnyOf(vararg ts: T) = asList().shouldContainAnyOf(ts)
fun <T> Collection<T>.shouldContainAnyOf(vararg ts: T) = this should containAnyOf(ts.asList())
fun <T> Array<T>.shouldNotContainAnyOf(vararg ts: T) = asList().shouldNotContainAnyOf(ts)
fun <T> Collection<T>.shouldNotContainAnyOf(vararg ts: T) = this shouldNot containAnyOf(ts.asList())
infix fun <T> Array<T>.shouldContainAnyOf(ts: Collection<T>) = asList().shouldContainAnyOf(ts)
infix fun <T> Collection<T>.shouldContainAnyOf(ts: Collection<T>) = this should containAnyOf(ts)
infix fun <T> Array<T>.shouldNotContainAnyOf(ts: Collection<T>) = asList().shouldNotContainAnyOf(ts)
infix fun <T> Collection<T>.shouldNotContainAnyOf(ts: Collection<T>) = this shouldNot containAnyOf(ts)

fun <T> containAnyOf(ts: Collection<T>) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>): MatcherResult {
    if (ts.isEmpty()) throwEmptyCollectionError()
    return MatcherResult(
            ts.any { it in value },
            { "Collection should contain any of ${ts.joinToString(separator = ", ", limit = 10) { stringRepr(it) }}" },
            { "Collection should not contain any of ${ts.joinToString(separator = ", ", limit = 10) { stringRepr(it) }}" }
    )
  }
}


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
 * Matcher that verifies that this instance is in [collection]
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
  override fun test(value: T): MatcherResult {
    if (collection.isEmpty()) throwEmptyCollectionError()

    val match = collection.any { it === value }
    return MatcherResult(match,
      "Collection should contain the instance of value, but doesn't.",
      "Collection should not contain the instance of value, but does.")
  }
}

/**
 * Verifies that this element is in [collection] by comparing value
 *
 * Assertion to check that this element is in [collection]. This assertion checks by value, and not by reference,
 * therefore even if the exact instance is not in [collection] but another instance with same value is present, the
 * test will pass.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
infix fun <T> T.shouldBeIn(collection: Collection<T>) = this should beIn(collection)

/**
 * Verifies that this element is NOT any of [collection]
 *
 * Assertion to check that this element is not any of [collection]. This assertion checks by value, and not by reference,
 * therefore any instance with same value must not be in [collection], or this will fail.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
infix fun <T> T.shouldNotBeIn(collection: Collection<T>) = this shouldNot beIn(collection.toList())

/**
 * Verifies that this element is any of [any] by comparing value
 *
 * Assertion to check that this element is any of [any]. This assertion checks by value, and not by reference,
 * therefore even if the exact instance is not any of [any] but another instance with same value is present, the
 * test will pass.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
fun <T> T.shouldBeIn(vararg any: T) = this should beIn(any.toList())

/**
 * Verifies that this element is NOT any of [any]
 *
 * Assertion to check that this element is not any of [any]. This assertion checks by value, and not by reference,
 * therefore any instance with same value must not be in [any], or this will fail.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
fun <T> T.shouldNotBeIn(vararg any: T) = this shouldNot beIn(any.toList())


/**
 * Verifies that this element is in [array] by comparing value
 *
 * Assertion to check that this element is in [array]. This assertion checks by value, and not by reference,
 * therefore even if the exact instance is not in [array] but another instance with same value is present, the
 * test will pass.
 *
 * An empty array will always fail. If you need to check for empty array, use [Array.shouldBeEmpty]
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
@JvmName("shouldBeInArray")
infix fun <T> T.shouldBeIn(array: Array<T>) = this should beIn(array.toList())

/**
 * Verifies that this element is NOT any of [array]
 *
 * Assertion to check that this element is not any of [array]. This assertion checks by value, and not by reference,
 * therefore any instance with same value must not be in [array], or this will fail.
 *
 * An empty array will always fail. If you need to check for empty array, use [Array.shouldBeEmpty]
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
@JvmName("shouldNotBeInArray")
infix fun <T> T.shouldNotBeIn(array: Array<T>) = this shouldNot beIn(array.toList())

/**
 *  Matcher that verifies that this element is in [collection] by comparing value
 *
 * Assertion to check that this element is in [collection]. This assertion checks by value, and not by reference,
 * therefore even if the exact instance is not in [collection] but another instance with same value is present, the
 * test will pass.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldBeOneOf]
 * @see [shouldNotBeOneOf]
 */
fun <T> beIn(collection: Collection<T>) = object : Matcher<T> {
  override fun test(value: T): MatcherResult {
    if (collection.isEmpty()) throwEmptyCollectionError()

    val match = value in collection
    return MatcherResult(match,
      "Collection should contain the element, but doesn't.",
      "Collection should not contain the element, but does.")
  }
}

private fun throwEmptyCollectionError(): Nothing {
  throw AssertionError("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
}

