package io.kotlintest.matchers.result

import io.kotlintest.Matcher
import io.kotlintest.should
import io.kotlintest.shouldNot

fun <T> Result<T>.shouldBeSuccess() = this should beSuccess()
fun <T> Result<T>.shouldNotBeSuccess() = this shouldNot beSuccess()
fun <T> beSuccess() = object : Matcher<Result<T>> {
  override fun test(value: Result<T>) = io.kotlintest.Result(
      value.isSuccess,
      "Result should be a success.",
      "Result should not be a success"
  )
}

infix fun <T> Result<T>.shouldBeSuccess(expected: T) = this should beSuccess(expected)
infix fun <T> Result<T>.shouldNotBeSuccess(expected: T) = this shouldNot beSuccess(expected)
fun <T> beSuccess(expected: T) = object : Matcher<Result<T>> {
  override fun test(value: Result<T>): io.kotlintest.Result {
    return value.fold(
        { result ->
          if (result == expected) {
            io.kotlintest.Result(true, "Result should be a Success($expected)", "Result should not be a Success($expected)")
          } else io.kotlintest.Result(false, "Result should be a success", "")
        },
        { error -> io.kotlintest.Result(false, "Result should be a success", "") }
    )
  }
}

fun <T> Result<T>.shouldBeFailure() = this should beFailure()
fun <T> Result<T>.shouldNotBeFailure() = this shouldNot beFailure()
fun <T> beFailure() = object : Matcher<Result<T>> {
  override fun test(value: Result<T>) = io.kotlintest.Result(
      value.isFailure,
      "Result should be a failure",
      "Result should not be a failure"
  )
}

inline fun <reified A : Throwable> Result<Any>.shouldBeFailureOfType() = this should beFailureOfType<A>()
inline fun <reified A : Throwable> Result<Any>.shouldNotBeFailureOfType() = this shouldNot beFailureOfType<A>()
inline fun <reified A : Throwable> beFailureOfType() = object : Matcher<Result<Any>> {
  override fun test(value: Result<Any>): io.kotlintest.Result {
    val error = value.exceptionOrNull()
    return when {
      value.isSuccess -> io.kotlintest.Result(false, "Result should be a failure", "")
      error is A -> io.kotlintest.Result(true, "Result should be a Failure(${A::class})", "Result should not be a Failure(${A::class})")
      else -> io.kotlintest.Result(false, "Result should be a Failure(${A::class}) but was Failure(${error!!::class})", "")
    }
  }
}