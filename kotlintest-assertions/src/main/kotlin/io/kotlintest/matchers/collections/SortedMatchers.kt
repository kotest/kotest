package io.kotlintest.matchers.collections

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot

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

fun <T : Comparable<T>> beSorted(): Matcher<List<T>> = sorted()
fun <T : Comparable<T>> sorted(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): Result {
    val failure = value.withIndex().firstOrNull { (i, it) -> i != value.lastIndex && it > value[i+1] }
    val snippet = value.joinToString(",", limit = 10)
    val elementMessage = when (failure) {
      null -> ""
      else -> ". Element ${failure.value} at index ${failure.index} was greater than element ${value[failure.index+1]}"
    }
    return Result(
            failure == null,
            "List [$snippet] should be sorted$elementMessage",
            "List [$snippet] should not be sorted"
    )
  }
}
