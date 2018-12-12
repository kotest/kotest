package io.kotlintest.matchers.collections

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot
import io.kotlintest.stringRepr

fun <T> Collection<T>.shouldBeEmpty() = this should beEmpty()
fun <T> Collection<T>.shouldNotBeEmpty() = this shouldNot beEmpty()

fun <T> beEmpty(): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>): Result = Result(
          value.isEmpty(),
          "Collection should be empty but contained ${stringRepr(value)}",
          "Collection should not be empty"
  )
}