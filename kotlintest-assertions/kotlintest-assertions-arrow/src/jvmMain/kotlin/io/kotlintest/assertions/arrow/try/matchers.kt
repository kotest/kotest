package io.kotlintest.assertions.arrow.`try`

import arrow.core.Success
import arrow.core.Try
import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.matchers.types.beInstanceOf2
import io.kotlintest.should
import io.kotlintest.shouldNot

fun <T> Try<T>.shouldBeSuccess() = this should beSuccess()
fun <T> Try<T>.shouldNotBeSuccess() = this shouldNot beSuccess()
fun <T> beSuccess() = beInstanceOf2<Try<T>, Success<T>>()

fun <T> Try<T>.shouldBeSuccess(t: T) = this should beSuccess(t)
fun <T> Try<T>.shouldNotBeSuccess(t: T) = this shouldNot beSuccess(t)
fun <A> beSuccess(a: A) = object : Matcher<Try<A>> {
  override fun test(value: Try<A>): Result {
    return when (value) {
      is Try.Failure -> Result(false,
          "Try should be a Success($a) but was Failure(${value.exception.message})",
          "")
      is Try.Success<*> -> {
        if (value.value == a)
          Result(true, "Try should be Success($a)", "Try should not be Success($a)")
        else
          Result(false, "Try should be Success($a) but was Success(${value.value})", "")
      }
    }
  }
}

fun Try<Any>.shouldBeFailure() = this should beFailure()
fun Try<Any>.shouldNotBeFailure() = this shouldNot beFailure()
fun beFailure() = object : Matcher<Try<Any>> {
  override fun test(value: Try<Any>): Result {
    return when (value) {
      is Try.Success<*> -> Result(false, "Try should be a Failure but was Success(${value.value})", "")
      is Try.Failure -> Result(true, "Try should be a Failure", "Try should not be Failure")
    }
  }
}

inline fun <reified A : Throwable> Try<Any>.shouldBeFailureOfType() = this should beFailureOfType<A>()
inline fun <reified A : Throwable> Try<Any>.shouldNotBeFailureOfType() = this shouldNot beFailureOfType<A>()
inline fun <reified A : Throwable> beFailureOfType() = object : Matcher<Try<Any>> {
  override fun test(value: Try<Any>): Result {
    return when (value) {
      is Try.Success<*> -> Result(false, "Try should be a Failure but was Success(${value.value})", "")
      is Try.Failure -> {
        if (value.exception is A)
          Result(true, "Try should be a Failure(${A::class})", "Try should not be Failure")
        else
          Result(false,
              "Try should be a Failure(${A::class}), but was Failure(${value.exception::class})",
              "Try should not be Failure")
      }
    }
  }
}
