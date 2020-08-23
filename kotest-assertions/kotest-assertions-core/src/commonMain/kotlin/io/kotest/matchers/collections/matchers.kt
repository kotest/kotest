package io.kotest.matchers.collections

import io.kotest.assertions.show.show
import io.kotest.matchers.*
import kotlin.jvm.JvmName

fun <T> Iterable<T>.shouldContainOnlyNulls() = toList().shouldContainOnlyNulls()
fun <T> Array<T>.shouldContainOnlyNulls() = asList().shouldContainOnlyNulls()
fun <T> Collection<T>.shouldContainOnlyNulls() = this should containOnlyNulls()
fun <T> Iterable<T>.shouldNotContainOnlyNulls() = toList().shouldNotContainOnlyNulls()
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

fun <T> Iterable<T>.shouldContainNull() = toList().shouldContainNull()
fun <T> Array<T>.shouldContainNull() = asList().shouldContainNull()
fun <T> Collection<T>.shouldContainNull() = this should containNull()
fun <T> Iterable<T>.shouldNotContainNull() = toList().shouldNotContainNull()
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

fun <T> Iterable<T>.shouldContainNoNulls() = toList().shouldContainNoNulls()
fun <T> Array<T>.shouldContainNoNulls() = asList().shouldContainNoNulls()
fun <T> Collection<T>.shouldContainNoNulls() = this should containNoNulls()
fun <T> Iterable<T>.shouldNotContainNoNulls() = toList().shouldNotContainNoNulls()
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

infix fun <T : Comparable<T>> Iterable<T>.shouldHaveUpperBound(t: T) = toList().shouldHaveUpperBound(t)
infix fun <T : Comparable<T>> Array<T>.shouldHaveUpperBound(t: T) = asList().shouldHaveUpperBound(t)
infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveUpperBound(t: T) = this should haveUpperBound(t)

fun <T : Comparable<T>, C : Collection<T>> haveUpperBound(t: T) = object : Matcher<C> {
   override fun test(value: C) = MatcherResult(
      value.all { it <= t },
      "Collection should have upper bound $t",
      "Collection should not have upper bound $t"
   )
}

infix fun <T : Comparable<T>> Iterable<T>.shouldHaveLowerBound(t: T) = toList().shouldHaveLowerBound(t)
infix fun <T : Comparable<T>> Array<T>.shouldHaveLowerBound(t: T) = asList().shouldHaveLowerBound(t)
infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveLowerBound(t: T) = this should haveLowerBound(t)
fun <T : Comparable<T>, C : Collection<T>> haveLowerBound(t: T) = object : Matcher<C> {
   override fun test(value: C) = MatcherResult(
      value.all { t <= it },
      "Collection should have lower bound $t",
      "Collection should not have lower bound $t"
   )
}

fun <T> Iterable<T>.shouldBeUnique() = toList().shouldBeUnique()
fun <T> Array<T>.shouldBeUnique() = asList().shouldBeUnique()
fun <T> Collection<T>.shouldBeUnique() = this should beUnique()
fun <T> Iterable<T>.shouldNotBeUnique() = toList().shouldNotBeUnique()
fun <T> Array<T>.shouldNotBeUnique() = asList().shouldNotBeUnique()
fun <T> Collection<T>.shouldNotBeUnique() = this shouldNot beUnique()
fun <T> beUnique() = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.toSet().size == value.size,
      "Collection should be Unique",
      "Collection should contain at least one duplicate element"
   )
}

fun <T> Iterable<T>.shouldContainDuplicates() = toList().shouldContainDuplicates()
fun <T> Array<T>.shouldContainDuplicates() = asList().shouldContainDuplicates()
fun <T> Collection<T>.shouldContainDuplicates() = this should containDuplicates()
fun <T> Iterable<T>.shouldNotContainDuplicates() = toList().shouldNotContainDuplicates()
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

fun <T : Comparable<T>> Iterable<T>.shouldBeSorted() = toList().shouldBeSorted()
fun <T : Comparable<T>> Array<T>.shouldBeSorted() = asList().shouldBeSorted()
fun <T : Comparable<T>> List<T>.shouldBeSorted() = this should beSorted<T>()
fun <T : Comparable<T>> Iterable<T>.shouldNotBeSorted() = toList().shouldNotBeSorted()
fun <T : Comparable<T>> Array<T>.shouldNotBeSorted() = asList().shouldNotBeSorted()
fun <T : Comparable<T>> List<T>.shouldNotBeSorted() = this shouldNot beSorted<T>()
infix fun <T> Iterable<T>.shouldBeSortedWith(comparator: Comparator<in T>) = toList().shouldBeSortedWith(comparator)
infix fun <T> Array<T>.shouldBeSortedWith(comparator: Comparator<in T>) = asList().shouldBeSortedWith(comparator)
infix fun <T> List<T>.shouldBeSortedWith(comparator: Comparator<in T>) = this should beSortedWith(comparator)
infix fun <T> Iterable<T>.shouldNotBeSortedWith(comparator: Comparator<in T>) = toList().shouldNotBeSortedWith(comparator)
infix fun <T> Array<T>.shouldNotBeSortedWith(comparator: Comparator<in T>) = asList().shouldNotBeSortedWith(comparator)
infix fun <T> List<T>.shouldNotBeSortedWith(comparator: Comparator<in T>) = this shouldNot beSortedWith(comparator)
infix fun <T> Iterable<T>.shouldBeSortedWith(cmp: (T, T) -> Int) = toList().shouldBeSortedWith(cmp)
infix fun <T> Array<T>.shouldBeSortedWith(cmp: (T, T) -> Int) = asList().shouldBeSortedWith(cmp)
infix fun <T> List<T>.shouldBeSortedWith(cmp: (T, T) -> Int) = this should beSortedWith(cmp)
infix fun <T> Iterable<T>.shouldNotBeSortedWith(cmp: (T, T) -> Int) = toList().shouldNotBeSortedWith(cmp)
infix fun <T> Array<T>.shouldNotBeSortedWith(cmp: (T, T) -> Int) = asList().shouldNotBeSortedWith(cmp)
infix fun <T> List<T>.shouldNotBeSortedWith(cmp: (T, T) -> Int) = this shouldNot beSortedWith(cmp)

fun <T : Comparable<T>> Iterable<T>.shouldBeMonotonicallyIncreasing() = toList().shouldBeMonotonicallyIncreasing()
fun <T : Comparable<T>> Array<T>.shouldBeMonotonicallyIncreasing() = asList().shouldBeMonotonicallyIncreasing()
fun <T : Comparable<T>> List<T>.shouldBeMonotonicallyIncreasing() = this should beMonotonicallyIncreasing<T>()
fun <T : Comparable<T>> Iterable<T>.shouldNotBeMonotonicallyIncreasing() = toList().shouldNotBeMonotonicallyIncreasing()
fun <T : Comparable<T>> Array<T>.shouldNotBeMonotonicallyIncreasing() = asList().shouldNotBeMonotonicallyIncreasing()
fun <T : Comparable<T>> List<T>.shouldNotBeMonotonicallyIncreasing() = this shouldNot beMonotonicallyIncreasing<T>()
fun <T> List<T>.shouldBeMonotonicallyIncreasingWith(comparator: Comparator<in T>) =
   this should beMonotonicallyIncreasingWith(comparator)

fun <T> Iterable<T>.shouldBeMonotonicallyIncreasingWith(comparator: Comparator<in T>) =
   toList().shouldBeMonotonicallyIncreasingWith(comparator)

fun <T> Array<T>.shouldBeMonotonicallyIncreasingWith(comparator: Comparator<in T>) =
   asList().shouldBeMonotonicallyIncreasingWith(comparator)

fun <T> List<T>.shouldNotBeMonotonicallyIncreasingWith(comparator: Comparator<in T>) =
   this shouldNot beMonotonicallyIncreasingWith(comparator)

fun <T> Iterable<T>.shouldNotBeMonotonicallyIncreasingWith(comparator: Comparator<in T>) =
   toList().shouldNotBeMonotonicallyIncreasingWith(comparator)

fun <T> Array<T>.shouldNotBeMonotonicallyIncreasingWith(comparator: Comparator<in T>) =
   asList().shouldNotBeMonotonicallyIncreasingWith(comparator)


fun <T : Comparable<T>> Iterable<T>.shouldBeMonotonicallyDecreasing() = toList().shouldBeMonotonicallyDecreasing()
fun <T : Comparable<T>> Array<T>.shouldBeMonotonicallyDecreasing() = asList().shouldBeMonotonicallyDecreasing()
fun <T : Comparable<T>> List<T>.shouldBeMonotonicallyDecreasing() = this should beMonotonicallyDecreasing<T>()
fun <T : Comparable<T>> Iterable<T>.shouldNotBeMonotonicallyDecreasing() = toList().shouldNotBeMonotonicallyDecreasing()
fun <T : Comparable<T>> Array<T>.shouldNotBeMonotonicallyDecreasing() = asList().shouldNotBeMonotonicallyDecreasing()
fun <T : Comparable<T>> List<T>.shouldNotBeMonotonicallyDecreasing() = this shouldNot beMonotonicallyDecreasing<T>()
fun <T> List<T>.shouldBeMonotonicallyDecreasingWith(comparator: Comparator<in T>) =
   this should beMonotonicallyDecreasingWith(comparator)

fun <T> Iterable<T>.shouldBeMonotonicallyDecreasingWith(comparator: Comparator<in T>) =
   toList().shouldBeMonotonicallyDecreasingWith(comparator)

fun <T> Array<T>.shouldBeMonotonicallyDecreasingWith(comparator: Comparator<in T>) =
   asList().shouldBeMonotonicallyDecreasingWith(comparator)

fun <T> List<T>.shouldNotBeMonotonicallyDecreasingWith(comparator: Comparator<in T>) =
   this shouldNot beMonotonicallyDecreasingWith(comparator)

fun <T> Iterable<T>.shouldNotBeMonotonicallyDecreasingWith(comparator: Comparator<in T>) =
   toList().shouldNotBeMonotonicallyDecreasingWith(comparator)

fun <T> Array<T>.shouldNotBeMonotonicallyDecreasingWith(comparator: Comparator<in T>) =
   asList().shouldNotBeMonotonicallyDecreasingWith(comparator)

fun <T : Comparable<T>> Iterable<T>.shouldBeStrictlyIncreasing() = toList().shouldBeStrictlyIncreasing()
fun <T : Comparable<T>> Array<T>.shouldBeStrictlyIncreasing() = asList().shouldBeStrictlyIncreasing()
fun <T : Comparable<T>> List<T>.shouldBeStrictlyIncreasing() = this should beStrictlyIncreasing<T>()
fun <T : Comparable<T>> Iterable<T>.shouldNotBeStrictlyIncreasing() = toList().shouldNotBeStrictlyIncreasing()
fun <T : Comparable<T>> Array<T>.shouldNotBeStrictlyIncreasing() = asList().shouldNotBeStrictlyIncreasing()
fun <T : Comparable<T>> List<T>.shouldNotBeStrictlyIncreasing() = this shouldNot beStrictlyIncreasing<T>()
fun <T> List<T>.shouldBeStrictlyIncreasingWith(comparator: Comparator<in T>) =
   this should beStrictlyIncreasingWith(comparator)

fun <T> Iterable<T>.shouldBeStrictlyIncreasingWith(comparator: Comparator<in T>) =
   toList().shouldBeStrictlyIncreasingWith(comparator)

fun <T> Array<T>.shouldBeStrictlyIncreasingWith(comparator: Comparator<in T>) =
   asList().shouldBeStrictlyIncreasingWith(comparator)

fun <T> List<T>.shouldNotBeStrictlyIncreasingWith(comparator: Comparator<in T>) =
   this shouldNot beStrictlyIncreasingWith(comparator)

fun <T> Iterable<T>.shouldNotBeStrictlyIncreasingWith(comparator: Comparator<in T>) =
   toList().shouldNotBeStrictlyIncreasingWith(comparator)

fun <T> Array<T>.shouldNotBeStrictlyIncreasingWith(comparator: Comparator<in T>) =
   asList().shouldNotBeStrictlyIncreasingWith(comparator)

fun <T : Comparable<T>> Iterable<T>.shouldBeStrictlyDecreasing() = toList().shouldBeStrictlyDecreasing()
fun <T : Comparable<T>> List<T>.shouldBeStrictlyDecreasing() = this should beStrictlyDecreasing<T>()
fun <T : Comparable<T>> Iterable<T>.shouldNotBeStrictlyDecreasing() = toList().shouldNotBeStrictlyDecreasing()
fun <T : Comparable<T>> List<T>.shouldNotBeStrictlyDecreasing() = this shouldNot beStrictlyDecreasing<T>()
fun <T> List<T>.shouldBeStrictlyDecreasingWith(comparator: Comparator<in T>) =
   this should beStrictlyDecreasingWith(comparator)

fun <T> Iterable<T>.shouldBeStrictlyDecreasingWith(comparator: Comparator<in T>) =
   toList().shouldBeStrictlyDecreasingWith(comparator)

fun <T> Array<T>.shouldBeStrictlyDecreasingWith(comparator: Comparator<in T>) =
   asList().shouldBeStrictlyDecreasingWith(comparator)

fun <T> List<T>.shouldNotBeStrictlyDecreasingWith(comparator: Comparator<in T>) =
   this shouldNot beStrictlyDecreasingWith(comparator)

fun <T> Iterable<T>.shouldNotBeStrictlyDecreasingWith(comparator: Comparator<in T>) =
   toList().shouldNotBeStrictlyDecreasingWith(comparator)

fun <T> Array<T>.shouldNotBeStrictlyDecreasingWith(comparator: Comparator<in T>) =
   asList().shouldNotBeStrictlyDecreasingWith(comparator)

infix fun <T> Iterable<T>.shouldHaveSingleElement(t: T) = toList().shouldHaveSingleElement(t)
infix fun <T> Array<T>.shouldHaveSingleElement(t: T) = asList().shouldHaveSingleElement(t)
infix fun <T> Iterable<T>.shouldHaveSingleElement(p: (T) -> Boolean) = toList().shouldHaveSingleElement(p)
infix fun <T> Array<T>.shouldHaveSingleElement(p: (T) -> Boolean) = asList().shouldHaveSingleElement(p)
infix fun <T> Collection<T>.shouldHaveSingleElement(t: T) = this should singleElement(t)
infix fun <T> Collection<T>.shouldHaveSingleElement(p: (T) -> Boolean) = this should singleElement(p)
infix fun <T> Iterable<T>.shouldNotHaveSingleElement(t: T) = toList().shouldNotHaveSingleElement(t)
infix fun <T> Array<T>.shouldNotHaveSingleElement(t: T) = asList().shouldNotHaveSingleElement(t)
infix fun <T> Collection<T>.shouldNotHaveSingleElement(t: T) = this shouldNot singleElement(t)
infix fun <T> Iterable<T>.shouldHaveSize(size: Int) = toList().shouldHaveSize(size)
infix fun <T> Array<T>.shouldHaveSize(size: Int) = asList().shouldHaveSize(size)
infix fun <T> Collection<T>.shouldHaveSize(size: Int) = this should haveSize(size = size)
infix fun <T> Iterable<T>.shouldNotHaveSize(size: Int) = toList().shouldNotHaveSize(size)
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

fun <T> Iterable<T>.shouldBeSingleton() = toList().shouldBeSingleton()
fun <T> Array<T>.shouldBeSingleton() = asList().shouldBeSingleton()

inline fun <T> Collection<T>.shouldBeSingleton(fn: (T) -> Unit) {
   this.shouldBeSingleton()
   fn(this.first())
}

inline fun <T> Iterable<T>.shouldBeSingleton(fn: (T) -> Unit) {
   toList().shouldBeSingleton(fn)
}

inline fun <T> Array<T>.shouldBeSingleton(fn: (T) -> Unit) {
   asList().shouldBeSingleton(fn)
}

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

fun <T> Iterable<T>.shouldNotBeSingleton() = toList().shouldNotBeSingleton()
fun <T> Array<T>.shouldNotBeSingleton() = asList().shouldNotBeSingleton()

infix fun <T, U> Iterable<T>.shouldBeLargerThan(other: Collection<U>) = toList().shouldBeLargerThan(other)
infix fun <T, U> Array<T>.shouldBeLargerThan(other: Collection<U>) = asList().shouldBeLargerThan(other)
infix fun <T, U> Iterable<T>.shouldBeLargerThan(other: Iterable<U>) = toList().shouldBeLargerThan(other.toList())
infix fun <T, U> Array<T>.shouldBeLargerThan(other: Array<U>) = asList().shouldBeLargerThan(other.asList())
infix fun <T, U> Collection<T>.shouldBeLargerThan(other: Collection<U>) = this should beLargerThan(other)
fun <T, U> beLargerThan(other: Collection<U>) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size > other.size,
      "Collection of size ${value.size} should be larger than collection of size ${other.size}",
      "Collection of size ${value.size} should not be larger than collection of size ${other.size}"
   )
}

infix fun <T, U> Iterable<T>.shouldBeSmallerThan(other: Collection<U>) = toList().shouldBeSmallerThan(other)
infix fun <T, U> Array<T>.shouldBeSmallerThan(other: Collection<U>) = asList().shouldBeSmallerThan(other)
infix fun <T, U> Iterable<T>.shouldBeSmallerThan(other: Iterable<U>) = toList().shouldBeSmallerThan(other.toList())
infix fun <T, U> Array<T>.shouldBeSmallerThan(other: Array<U>) = asList().shouldBeSmallerThan(other.asList())
infix fun <T, U> Collection<T>.shouldBeSmallerThan(other: Collection<U>) = this should beSmallerThan(other)
fun <T, U> beSmallerThan(other: Collection<U>) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size < other.size,
      "Collection of size ${value.size} should be smaller than collection of size ${other.size}",
      "Collection of size ${value.size} should not be smaller than collection of size ${other.size}"
   )
}

infix fun <T, U> Iterable<T>.shouldBeSameSizeAs(other: Collection<U>) = toList().shouldBeSameSizeAs(other)
infix fun <T, U> Array<T>.shouldBeSameSizeAs(other: Collection<U>) = asList().shouldBeSameSizeAs(other)
infix fun <T, U> Iterable<T>.shouldBeSameSizeAs(other: Iterable<U>) = toList().shouldBeSameSizeAs(other.toList())
infix fun <T, U> Array<T>.shouldBeSameSizeAs(other: Array<U>) = asList().shouldBeSameSizeAs(other.asList())
infix fun <T, U> Collection<T>.shouldBeSameSizeAs(other: Collection<U>) = this should beSameSizeAs(other)
fun <T, U> beSameSizeAs(other: Collection<U>) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size == other.size,
      "Collection of size ${value.size} should be the same size as collection of size ${other.size}",
      "Collection of size ${value.size} should not be the same size as collection of size ${other.size}"
   )
}

infix fun <T> Iterable<T>.shouldHaveAtLeastSize(n: Int) = toList().shouldHaveAtLeastSize(n)
infix fun <T> Array<T>.shouldHaveAtLeastSize(n: Int) = asList().shouldHaveAtLeastSize(n)
infix fun <T> Collection<T>.shouldHaveAtLeastSize(n: Int) = this shouldHave atLeastSize(n)
fun <T> atLeastSize(n: Int) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size >= n,
      "Collection should contain at least $n elements",
      "Collection should contain less than $n elements"
   )
}

infix fun <T> Iterable<T>.shouldHaveAtMostSize(n: Int) = toList().shouldHaveAtMostSize(n)
infix fun <T> Array<T>.shouldHaveAtMostSize(n: Int) = asList().shouldHaveAtMostSize(n)
infix fun <T> Collection<T>.shouldHaveAtMostSize(n: Int) = this shouldHave atMostSize(n)
fun <T> atMostSize(n: Int) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size <= n,
      "Collection should contain at most $n elements",
      "Collection should contain more than $n elements"
   )
}

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
infix fun <T> Iterable<T>.shouldNotExistInOrder(expected: Iterable<(T) -> Boolean>) = toList().shouldNotExistInOrder(expected.toList())
infix fun <T> Array<T>.shouldNotExistInOrder(expected: Array<(T) -> Boolean>) = asList().shouldNotExistInOrder(expected.asList())
infix fun <T> Iterable<T>.shouldNotExistInOrder(expected: List<(T) -> Boolean>) = toList().shouldNotExistInOrder(expected)
infix fun <T> Array<T>.shouldNotExistInOrder(expected: List<(T) -> Boolean>) = asList().shouldNotExistInOrder(expected)
infix fun <T> List<T>.shouldNotExistInOrder(expected: List<(T) -> Boolean>) = this shouldNot existInOrder(expected)

fun <T> Iterable<T>.shouldBeEmpty() = toList().shouldBeEmpty()
fun <T> Array<T>.shouldBeEmpty() = asList().shouldBeEmpty()
fun <T> Collection<T>.shouldBeEmpty() = this should beEmpty()
fun <T> Iterable<T>.shouldNotBeEmpty() = toList().shouldNotBeEmpty()
fun <T> Array<T>.shouldNotBeEmpty() = asList().shouldNotBeEmpty()
fun <T> Collection<T>.shouldNotBeEmpty() = this shouldNot beEmpty()

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
      return MatcherResult(
         match,
         "Collection should contain the instance of value, but doesn't.",
         "Collection should not contain the instance of value, but does."
      )
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

      return MatcherResult(
         match,
         "Collection should contain ${value.show().value}, but doesn't. Possible values: ${collection.show().value}",
         "Collection should not contain ${value.show().value}, but does. Forbidden values: ${collection.show().value}"
      )
   }
}

private fun throwEmptyCollectionError(): Nothing {
   throw AssertionError("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
}

