package io.kotlintest.assertions.arrow.nel

import arrow.data.NonEmptyList
import io.kotlintest.Matcher
import io.kotlintest.Result

fun <T> containOnlyNulls() = object : Matcher<NonEmptyList<T>> {
  override fun test(value: NonEmptyList<T>) =
      Result(
          value.all.all { it == null },
          "NonEmptyList should contain only nulls",
          "NonEmptyList should not contain only nulls"
      )
}

/**
 * `collection should containNull()` tests that the collection
 * contains at least one null
 */
fun <T> containNull() = object : Matcher<NonEmptyList<T>> {
  override fun test(value: NonEmptyList<T>) =
      Result(
          value.all.any { it == null },
          "NonEmptyList should contain at least one null",
          "NonEmptyList should not contain any nulls"
      )
}

fun <T> haveElementAt(index: Int, element: T) = object : Matcher<NonEmptyList<T>> {
  override fun test(value: NonEmptyList<T>) =
      Result(
          value.all[index] == element,
          "NonEmptyList should contain $element at index $index",
          "NonEmptyList should not contain $element at index $index"
      )
}

fun <T> containNoNulls() = object : Matcher<NonEmptyList<T>> {
  override fun test(value: NonEmptyList<T>) =
      Result(
          value.all.all { it != null },
          "NonEmptyList should not contain nulls",
          "NonEmptyList should have at least one null"
      )
}

fun <T> contain(t: T) = object : Matcher<NonEmptyList<T>> {
  override fun test(value: NonEmptyList<T>) = Result(
      value.all.contains(t),
      "NonEmptyList should contain element $t",
      "NonEmptyList should not contain element $t"
  )
}

fun <T> haveDuplicates() = object : Matcher<NonEmptyList<T>> {
  override fun test(value: NonEmptyList<T>) = Result(
      value.all.toSet().size < value.size,
      "NonEmptyList should contain duplicates",
      "NonEmptyList should not contain duplicates"
  )
}

fun <T> containAll(vararg ts: T) = containAll(ts.asList())
fun <T> containAll(ts: List<T>): Matcher<NonEmptyList<T>> = object : Matcher<NonEmptyList<T>> {
  override fun test(value: NonEmptyList<T>) = Result(
      ts.all { value.contains(it) },
      "NonEmptyList should contain all of ${ts.take(10).joinToString(",")}",
      "NonEmptyList should not contain all of ${ts.take(10).joinToString(",")}"
  )
}

fun <T> haveSize(size: Int): Matcher<NonEmptyList<T>> = object : Matcher<NonEmptyList<T>> {
  override fun test(value: NonEmptyList<T>) =
      Result(
          value.size == size,
          "NonEmptyList should have size $size but has size ${value.size}",
          "NonEmptyList should not have size $size"
      )
}

fun <T> singleElement(t: T): Matcher<NonEmptyList<T>> = object : Matcher<NonEmptyList<T>> {
  override fun test(value: NonEmptyList<T>) = Result(
      value.size == 1 && value.head == t,
      "NonEmptyList should be a single element of $t but has ${value.size} elements",
      "NonEmptyList should not be a single element of $t"
  )
}

fun <T : Comparable<T>> sorted(): Matcher<NonEmptyList<T>> = object : Matcher<NonEmptyList<T>> {
  override fun test(value: NonEmptyList<T>): Result {
    val passed = value.all.sorted() == value.all
    val snippet = if (value.size <= 10) value.all.joinToString(",") else value.all.take(10).joinToString(",") + "..."
    return Result(
        passed,
        "NonEmptyList $snippet should be sorted",
        "NonEmptyList $snippet should not be sorted"
    )
  }
}