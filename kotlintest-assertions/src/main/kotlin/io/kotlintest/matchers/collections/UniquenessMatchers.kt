package io.kotlintest.matchers.collections

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot

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