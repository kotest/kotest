package io.kotlintest.assertions.arrow.option

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot

fun <T> Option<T>.shouldBeSome(t: T) = this should beSome(t)
fun <T> Option<T>.shouldNotBeSome(t: T) = this shouldNot beSome(t)
fun <T> beSome(t: T) = object : Matcher<Option<T>> {
  override fun test(value: Option<T>): Result {
    return when (value) {
      is None -> {
        Result(false, "Option should be Some($t) but was None", "")
      }
      is Some -> {
        if (value.t == t)
          Result(true, "Option should be Some($t)", "Option should not be Some($t)")
        else
          Result(false, "Option should be Some($t) but was Some(${value.t})", "")
      }
    }
  }
}

fun Option<Any>.shouldBeNone() = this should beNone()
fun Option<Any>.shouldNotBeNone() = this shouldNot beNone()
fun beNone() = object : Matcher<Option<Any>> {
  override fun test(value: Option<Any>): Result {
    return when (value) {
      is None -> {
        Result(true, "Option should be None", "Option should not be None")
      }
      is Some -> {
        Result(false, "Option should be None but was Some(${value.t})", "")
      }
    }
  }
}