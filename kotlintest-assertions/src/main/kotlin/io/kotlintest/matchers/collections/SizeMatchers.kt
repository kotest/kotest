package io.kotlintest.matchers.collections

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldHave
import io.kotlintest.shouldNot

infix fun <T> Collection<T>.shouldHaveSize(size: Int) = this should haveSize(size)
infix fun <T> Collection<T>.shouldNotHaveSize(size: Int) = this shouldNot haveSize(size)

fun <T> haveSize(size: Int): Matcher<Collection<T>> = haveSizeMatcher(size)

fun <T> haveSizeMatcher(size: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
          Result(
                  value.size == size,
                  "Collection should have size $size but has size ${value.size}",
                  "Collection should not have size $size"
          )
}

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