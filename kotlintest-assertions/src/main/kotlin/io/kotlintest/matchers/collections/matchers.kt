package io.kotlintest.matchers.collections

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.beSorted
import io.kotlintest.matchers.containAll
import io.kotlintest.matchers.containsInOrder
import io.kotlintest.matchers.haveSize
import io.kotlintest.matchers.singleElement
import io.kotlintest.should
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
fun <T> haveElementAt(index: Int, element: T) = object : Matcher<List<T>> {
  override fun test(value: List<T>) =
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

fun <T> Collection<T>.shouldContain(t: T) = this should contain(t)
fun <T> Collection<T>.shouldNotContain(t: T) = this shouldNot contain(t)
fun <T> contain(t: T) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.contains(t),
      "Collection should contain element $t",
      "Collection should not contain element $t"
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

fun <T : Comparable<T>> List<T>.shouldContainInOrder(expected: List<T>) = this should containsInOrder(expected)
fun <T : Comparable<T>> List<T>.shouldNotContainInOrder(expected: List<T>) = this shouldNot containsInOrder(expected)

fun <T> Collection<T>.shouldBeEmpty() = this should beEmpty()
fun <T> Collection<T>.shouldNotBeEmpty() = this shouldNot beEmpty()

fun <T> Collection<T>.shouldContainAll(ts: Collection<T>) = this should containAll(ts)
fun <T> Collection<T>.shouldNotContainAll(ts: Collection<T>) = this shouldNot containAll(ts)