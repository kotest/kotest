package io.kotlintest.assertions.arrow.`try`

import arrow.data.Try
import io.kotlintest.Matcher
import io.kotlintest.Result

fun <A> success(a: A) = object : Matcher<Try<A>> {
  override fun test(value: Try<A>): Result {
    return when (value) {
      is Try.Failure<*> -> Result(false, "Try should be a Success($a) but was Failure(${value.exception.message})", "")
      is Try.Success<*> -> {
        if (value.value == a)
          Result(true, "Try should be Success($a)", "Try should not be Success($a)")
        else
          Result(false, "Try should be Success($a) but was Success(${value.value})", "")
      }
    }
  }
}

fun failure() = object : Matcher<Try<Any>> {
  override fun test(value: Try<Any>): Result {
    return when (value) {
      is Try.Success<*> -> Result(false, "Try should be a Failure but was Success(${value.value})", "")
      is Try.Failure<*> -> Result(true, "Try should be a Failure", "Try should not be Failure")
    }
  }
}