package io.kotlintest.matchers.result

import io.kotlintest.*
import io.kotlintest.ErrorCollector
import kotlin.Result

fun <T> Result<T>.shouldBeSuccess(block: ((T?) -> Unit)? = null) {
  BeSuccess<T>().test(this)
  block?.invoke(getOrNull())
}

fun <T> Result<T>.shouldNotBeSuccess() = test(inverse = true) { BeSuccess<T>().test(this) }

infix fun <T> Result<T>.shouldBeSuccess(expected: T) = test { BeSuccess<T>(expected).test(this) }
infix fun <T> Result<T>.shouldNotBeSuccess(expected: T) = test(inverse = true) { BeSuccess<T>(expected).test(this) }
class BeSuccess<T>(val expected: T? = null) : Matcher<Result<T>> {
  override fun test(value: Result<T>): MatcherResult {
    return when {
      !value.isSuccess -> defaultResult(false)
      expected == null -> defaultResult(true)
      else -> io.kotlintest.MatcherResult(
        value.getOrNull() == expected,
        "Result should be a Success($expected), but instead got Succes(${value.getOrNull()}).",
        "Result should not be a Success($expected)"
      )
    }
  }

  private fun defaultResult(passed: Boolean) =
    io.kotlintest.MatcherResult(passed, "Result should be a success.", "Result should not be a success")
}

fun Result<Any>.shouldBeFailure(block: ((Throwable?) -> Unit)? = null) {
  test { BeFailure().test(this) }
  block?.invoke(exceptionOrNull())
}

fun Result<Any>.shouldNotBeFailure() = test(inverse = true) { BeFailure().test(this) }
class BeFailure : Matcher<Result<Any>> {
  override fun test(value: Result<Any>) = io.kotlintest.MatcherResult(
    value.isFailure,
    "Result should be a failure",
    "Result should not be a failure"
  )
}

inline fun <reified A : Throwable> Result<Any>.shouldBeFailureOfType() = test { BeFailureOfType(A::class.java).test(this) }
inline fun <reified A : Throwable> Result<Any>.shouldNotBeFailureOfType() = test(inverse = true) {
  BeFailureOfType(A::class.java).test(this)
}

class BeFailureOfType<A : Throwable>(private val clazz: Class<A>) : Matcher<Result<Any>> {
  override fun test(value: Result<Any>): MatcherResult {
    val error = value.exceptionOrNull()
    return when {
      value.isSuccess -> MatcherResult(false, "Result should be a failure", "")
      clazz.isInstance(error) -> MatcherResult(true,
        "Result should be a Failure($clazz)",
        "Result should not be a Failure($clazz)")
      else -> {
        MatcherResult(false,
          "Result should be a Failure($clazz) but was Failure(${error!!::class})",
          "")
      }
    }
  }
}

@PublishedApi
internal fun test(inverse: Boolean = false, block: () -> MatcherResult) {
  val result = block()
  if ((inverse && result.passed()) || (!inverse && !result.passed())) {
    ErrorCollector.collectOrThrow(
      Failures.failure(ErrorCollector.clueContextAsString() + if (inverse) result.negatedFailureMessage() else result.failureMessage())
    )
  }
}
