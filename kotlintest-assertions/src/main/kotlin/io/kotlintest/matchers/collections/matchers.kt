package io.kotlintest.matchers.collections

import io.kotlintest.Matcher
import io.kotlintest.Result

fun <T> containOnlyNulls() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
      Result(
          value.all { it == null },
          "Collection should contain only nulls",
          "Collection should not contain only nulls"
      )
}

/**
 * `collection should containNull()` tests that the collection
 * contains at least one null
 */
fun <T> containNull() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
      Result(
          value.any { it == null },
          "Collection should contain at least one null",
          "Collection should not contain any nulls"
      )
}

fun <T> haveElementAt(index: Int, element: T) = object : Matcher<List<T>> {
  override fun test(value: List<T>) =
      Result(
          value[index] == element,
          "Collection should contain $element at index $index",
          "Collection should not contain $element at index $index"
      )
}

fun <T> containNoNulls() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
      Result(
          value.all { it != null },
          "Collection should not contain nulls",
          "Collection should have at least one null"
      )
}

fun <T> contain(t: T) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.contains(t),
      "Collection should contain element $t",
      "Collection should not contain element $t"
  )
}

fun <T> haveDuplicates() = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.toSet().size < value.size,
      "Collection should contain duplicates",
      "Collection should not contain duplicates"
  )
}