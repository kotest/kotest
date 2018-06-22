package io.kotlintest.matchers.collections

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.beSorted
import io.kotlintest.matchers.containAll
import io.kotlintest.matchers.containsInOrder
import io.kotlintest.matchers.haveSize
import io.kotlintest.matchers.singleElement
import io.kotlintest.neverNullMatcher
import io.kotlintest.should
import io.kotlintest.shouldHave
import io.kotlintest.shouldNot

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

fun <T> List<T>.shouldHaveElementAt(index: Int, element: T) = this should haveElementAt(index, element)

fun <T> List<T>.shouldNotHaveElementAt(index: Int, element: T) = this shouldNot haveElementAt(index, element)

@Deprecated("Use shouldHaveElementAt", ReplaceWith("this.shouldHaveElementAt(index, element)", "io.kotlintest.should"))
fun <T, L : List<T>> L.shouldContainElementAt(index: Int, element: T) = this should haveElementAt(index, element)

@Deprecated("Use shouldNotHaveElementAt", ReplaceWith("this.shouldNotHaveElementAt(index, element)", "io.kotlintest.should"))
fun <T, L : List<T>> L.shouldNotContainElementAt(index: Int, element: T) = this shouldNot haveElementAt(index, element)

fun <T, L : List<T>> haveElementAt(index: Int, element: T) = object : Matcher<L> {
  override fun test(value: L) =
      Result(
          value[index] == element,
          "Collection should contain $element at index $index",
          "Collection should not contain $element at index $index"
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

fun <T, C : Collection<T>> C.shouldContain(t: T) = this should contain(t)
fun <T, C : Collection<T>> C.shouldNotContain(t: T) = this shouldNot contain(t)
fun <T, C : Collection<T>> contain(t: T) = object : Matcher<C> {
  override fun test(value: C) = Result(
      value.contains(t),
      "Collection should contain element $t",
      "Collection should not contain element $t"
  )
}

fun <T, C : Collection<T>> C?.shouldNotContainExactly(expected: C) = this shouldNot containExactly(expected)
fun <T, C : Collection<T>> C?.shouldNotContainExactly(vararg expected: T) = this shouldNot containExactly(*expected)
fun <T, C : Collection<T>> C?.shouldContainExactly(expected: C) = this should containExactly(expected)
fun <T, C : Collection<T>> C?.shouldContainExactly(vararg expected: T) = this should containExactly(*expected)
fun <T> containExactly(vararg expected: T): Matcher<Collection<T>?> = containExactly(expected.asList())
/** Assert that a collection contains exactly the given values and nothing else, in order. */
fun <T, C : Collection<T>> containExactly(expected: C): Matcher<C?> = neverNullMatcher { value ->
  val passed = value.size == expected.size && value.zip(expected) { a, b -> a == b }.all { it }
  Result(
      passed,
      "Collection should be exactly $expected but was $value",
      "Collection should not be exactly $expected"
  )
}

fun <T, C : Collection<T>> C?.shouldNotContainExactlyInAnyOrder(expected: C) = this shouldNot containExactlyInAnyOrder(expected)
fun <T, C : Collection<T>> C?.shouldNotContainExactlyInAnyOrder(vararg expected: T) = this shouldNot containExactlyInAnyOrder(*expected)
fun <T, C : Collection<T>> C?.shouldContainExactlyInAnyOrder(expected: C) = this should containExactlyInAnyOrder(expected)
fun <T, C : Collection<T>> C?.shouldContainExactlyInAnyOrder(vararg expected: T) = this should containExactlyInAnyOrder(*expected)
fun <T> containExactlyInAnyOrder(vararg expected: T): Matcher<Collection<T>?> = containExactlyInAnyOrder(expected.asList())
/** Assert that a collection contains exactly the given values and nothing else, in any order. */
fun <T, C : Collection<T>> containExactlyInAnyOrder(expected: C): Matcher<C?> = neverNullMatcher { value ->
  val passed = value.size == expected.size && expected.all { value.contains(it) }
  Result(
      passed,
      "Collection should contain $expected in any order, but was $value",
      "Collection should not contain exactly $expected in any order"
  )
}

fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveUpperBound(t: T) = this should haveUpperBound(t)
fun <T : Comparable<T>, C : Collection<T>> haveUpperBound(t: T) = object : Matcher<C> {
  override fun test(value: C) = Result(
      value.all { it <= t },
      "Collection should have upper bound $t",
      "Collection should not have upper bound $t"
  )
}

fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveLowerBound(t: T) = this should haveLowerBound(t)
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

fun <T : Comparable<T>> List<T>.shouldBeSorted() = this should beSorted<T>()
fun <T : Comparable<T>> List<T>.shouldNotBeSorted() = this shouldNot beSorted<T>()

fun <T> Collection<T>.shouldHaveSingleElement(t: T) = this should singleElement(t)
fun <T> Collection<T>.shouldNotHaveSingleElement(t: T) = this shouldNot singleElement(t)
fun <T> Collection<T>.shouldHaveSize(size: Int) = this should haveSize(size)
fun <T> Collection<T>.shouldNotHaveSize(size: Int) = this shouldNot haveSize(size)

fun <T, U> Collection<T>.shouldBeLargerThan(other: Collection<U>) = this should beLargerThan(other)
fun <T, U> beLargerThan(other: Collection<U>) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.size > other.size,
      "Collection of size ${value.size} should be larger than collection of size ${other.size}",
      "Collection of size ${value.size} should not be larger than collection of size ${other.size}"
  )
}

fun <T, U> Collection<T>.shouldBeSmallerThan(other: Collection<U>) = this should beSmallerThan(other)
fun <T, U> beSmallerThan(other: Collection<U>) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.size < other.size,
      "Collection of size ${value.size} should be smaller than collection of size ${other.size}",
      "Collection of size ${value.size} should not be smaller than collection of size ${other.size}"
  )
}

fun <T, U> Collection<T>.shouldBeSameSizeAs(other: Collection<U>) = this should beSameSizeAs(other)
fun <T, U> beSameSizeAs(other: Collection<U>) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.size == other.size,
      "Collection of size ${value.size} should be the same size as collection of size ${other.size}",
      "Collection of size ${value.size} should not be the same size as collection of size ${other.size}"
  )
}

fun <T> Collection<T>.shouldHaveAtLeastSize(n: Int) = this shouldHave atLeastSize(n)
fun <T> atLeastSize(n: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.size >= n,
      "Collection should contain at least $n elements",
      "Collection should contain less than $n elements"
  )
}

fun <T> Collection<T>.shouldHaveAtMostSize(n: Int) = this shouldHave atMostSize(n)
fun <T> atMostSize(n: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.size <= n,
      "Collection should contain at most $n elements",
      "Collection should contain more than $n elements"
  )
}

fun <T> Collection<T>.shouldExist(p: (T) -> Boolean) = this should exist(p)
fun <T> exist(p: (T) -> Boolean) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.any { p(it) },
      "Collection should contain an element that matches the predicate $p",
      "Collection should not contain an element that matches the predicate $p"
  )
}

fun <T : Comparable<T>> List<T>.shouldContainInOrder(vararg ts: T) = this.shouldContainInOrder(ts.toList())
fun <T : Comparable<T>> List<T>.shouldContainInOrder(expected: List<T>) = this should containsInOrder(expected)
fun <T : Comparable<T>> List<T>.shouldNotContainInOrder(expected: List<T>) = this shouldNot containsInOrder(expected)

fun <T> Collection<T>.shouldBeEmpty() = this should beEmpty()
fun <T> Collection<T>.shouldNotBeEmpty() = this shouldNot beEmpty()

fun <T> Collection<T>.shouldContainAll(vararg ts: T) = this should containAll(*ts)
fun <T> Collection<T>.shouldNotContainAll(vararg ts: T) = this shouldNot containAll(*ts)
fun <T> Collection<T>.shouldContainAll(ts: Collection<T>) = this should containAll(ts)
fun <T> Collection<T>.shouldNotContainAll(ts: Collection<T>) = this shouldNot containAll(ts)
