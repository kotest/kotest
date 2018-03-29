package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result

fun <T> haveSizeMatcher(size: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
      Result(
          value.size == size,
          "Collection should have size $size but has size ${value.size}",
          "Collection should not have size $size"
      )
}

fun <T> containsMatcher(t: T) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.contains(t),
      "Collection should contain element $t",
      "Collection should not contain element $t"
  )
}

fun <T, U : Collection<T>> beEmpty(): Matcher<U> = object : Matcher<U> {
  override fun test(value: U): Result = Result(
      value.isEmpty(),
      "Collection should be empty",
      "Collection should not be empty"
  )
}

@Deprecated("use containAll", ReplaceWith("containsAll(ts.asList())"))
fun <T> containsAll(vararg ts: T) = containAll(ts.asList())

@Deprecated("use containAll", ReplaceWith("containAll(ts)"))
fun <T> containsAll(ts: List<T>): Matcher<Collection<T>> = containAll(ts)

fun <T> containAll(vararg ts: T) = containAll(ts.asList())
fun <T> containAll(ts: List<T>): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      ts.all { value.contains(it) },
      "Collection should contain all of ${ts.take(10).joinToString(",")}",
      "Collection should not contain all of ${ts.take(10).joinToString(",")}"
  )
}

// should contain the expected list in order, but allows duplicates,
// so listOf(1, 2, 2, 3, 3, 3, 4, 4) should containInOrder(listOf(1,4)) is true
fun <T : Comparable<T>> containsInOrder(vararg ts: T) = containsInOrder(ts.asList())

fun <T : Comparable<T>> containsInOrder(expected: List<T>) = object : Matcher<List<T>> {
  override fun test(value: List<T>): Result {
    assert(expected.isNotEmpty(), { "expected values must not be empty" })
    assert(expected.sorted() == expected, { "expected values must be sorted but was $expected" })

    var cursor = 0
    var passed = true
    // to pass, all the indexes of element n must occur before the indexes of element n+1,n+2,...
    expected.forEach { expected ->
      val indexes = value.withIndex().filter { it.value == expected }.map { it.index }
      if (indexes.isEmpty()) {
        passed = false
      }
      indexes.forEach {
        if (passed && it < cursor) passed = false
        else cursor = indexes.max()!!
      }
    }

    val failureMessage = "[$value] did not contain the same elements in order as [$expected]"
    val negatedFailureMessage = "[$value] should not contain the same elements in order as [$expected]"
    return Result(passed, failureMessage, negatedFailureMessage)
  }
}

fun <T> haveSize(size: Int): Matcher<Collection<T>> = haveSizeMatcher(size)

fun <T> contain(t: T): Matcher<Collection<T>> = containsMatcher(t)

fun <T> singleElement(t: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.size == 1 && value.first() == t,
      "Collection should be a single element of $t but has ${value.size} elements",
      "Collection should not be a single element of $t"
  )
}

fun <T : Comparable<T>> sorted(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): Result {
    val passed = value.sorted() == value
    val snippet = if (value.size <= 10) value.joinToString(",") else value.take(10).joinToString(",") + "..."
    return Result(
        passed,
        "Collection $snippet should be sorted",
        "Collection $snippet should not be sorted"
    )
  }
}