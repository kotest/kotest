package io.kotlintest.matchers.collections

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot
import io.kotlintest.stringRepr

infix fun <T> List<T>.shouldStartWith(slice: Collection<T>) = this should startWith(slice)
infix fun <T> List<T>.shouldNotStartWith(slice: Collection<T>) = this shouldNot startWith(slice)
fun <T> startWith(slice: Collection<T>) = object : Matcher<List<T>> {
  override fun test(value: List<T>) =
      Result(
          value.subList(0, slice.size) == slice,
          "List should start with ${stringRepr(slice)}",
          "List should not start with ${stringRepr(slice)}"
      )
}

infix fun <T> List<T>.shouldEndWith(slice: Collection<T>) = this should endWith(slice)
infix fun <T> List<T>.shouldNotEndWith(slice: Collection<T>) = this shouldNot endWith(slice)
fun <T> endWith(slice: Collection<T>) = object : Matcher<List<T>> {
  override fun test(value: List<T>) =
      Result(
          value.subList(value.size - slice.size, value.size) == slice,
          "List should end with ${stringRepr(slice)}",
          "List should not end with ${stringRepr(slice)}"
      )
}

