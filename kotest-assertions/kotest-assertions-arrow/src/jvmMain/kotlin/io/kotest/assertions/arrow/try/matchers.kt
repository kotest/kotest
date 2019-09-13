package io.kotest.assertions.arrow.`try`

import arrow.core.Success
import arrow.core.Try
import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.equalityMatcher
import io.kotest.matchers.beInstanceOf2
import io.kotest.should
import io.kotest.shouldNot
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@UseExperimental(ExperimentalContracts::class)
fun <T> Try<T>.shouldBeSuccess() {
  contract {
    returns() implies (this@shouldBeSuccess is Try.Success<*>)
  }
  this should beSuccess()
}

fun <T> Try<T>.shouldNotBeSuccess() = this shouldNot beSuccess()
fun <T> beSuccess() = beInstanceOf2<Try<T>, Success<T>>()

infix fun <T> Try<T>.shouldBeSuccess(t: T) = this should beSuccess(t)

infix fun <T> Try<T>.shouldNotBeSuccess(t: T) = this shouldNot beSuccess(t)
fun <A> beSuccess(a: A) = object : Matcher<Try<A>> {
  override fun test(value: Try<A>): MatcherResult {
    return when (value) {
      is Try.Failure -> MatcherResult(false, "Try should be a Success($a) but was Failure(${value.exception.message})", "")
      is Try.Success<*> -> {
        if (value.value == a)
          MatcherResult(true, "Try should be Success($a)", "Try should not be Success($a)")
        else
          MatcherResult(false, "Try should be Success($a) but was Success(${value.value})", "")
      }
    }
  }
}

infix fun <T> Try<T>.shouldBeSuccess(valueTest: (T) -> Unit): Unit = this should beSuccess(valueTest)
fun <A> beSuccess(valueTest: (A) -> Unit): Matcher<Try<A>> = object : Matcher<Try<A>> {
  override fun test(value: Try<A>) = when (value) {
    is Try.Failure -> MatcherResult(passed = false, failureMessage = "Try should be a Success but was a Failure", negatedFailureMessage = "Try should not be a Success")
    is Try.Success<A> -> {
      valueTest(value.value)
      MatcherResult(passed = true, failureMessage = "", negatedFailureMessage = "")
    }
  }
}

@UseExperimental(ExperimentalContracts::class)
fun Try<Any>.shouldBeFailure() {
  contract {
    returns() implies (this@shouldBeFailure is Try.Failure)
  }
  this should beFailure()
}

fun Try<Any>.shouldNotBeFailure() = this shouldNot beFailure()
fun beFailure() = object : Matcher<Try<Any>> {
  override fun test(value: Try<Any>): MatcherResult {
    return when (value) {
      is Try.Success<*> -> MatcherResult(false, "Try should be a Failure but was Success(${value.value})", "")
      is Try.Failure -> MatcherResult(true, "Try should be a Failure", "Try should not be Failure")
    }
  }
}


infix fun Try<Any>.shouldBeFailure(expectedFailure: Throwable) = this should beFailure(expectedFailure)
fun beFailure(expectedFailure: Throwable): Matcher<Try<Any>> = object : Matcher<Try<Any>> {
  override fun test(value: Try<Any>): MatcherResult = when (value) {
    is Try.Success<*> -> MatcherResult(false, "Try should be a Failure but was Success(${value.value})", "")
    is Try.Failure -> equalityMatcher(expectedFailure).test(value.exception)
  }
}

infix fun Try<Any>.shouldBeFailure(throwableTest: (Throwable) -> Unit) = this should beFailure(throwableTest)
fun beFailure(throwableTest: (Throwable) -> Unit): Matcher<Try<Any>> = object : Matcher<Try<Any>> {
  override fun test(value: Try<Any>): MatcherResult = when (value) {
    is Try.Success<*> -> MatcherResult(false, "Try should be a Failure but was Success(${value.value})", "Try should not be a Failure")
    is Try.Failure -> {
      throwableTest(value.exception)
      MatcherResult(true, "", "")
    }
  }
}

inline fun <reified A : Throwable> Try<Any>.shouldBeFailureOfType() = this should beFailureOfType<A>()
inline fun <reified A : Throwable> Try<Any>.shouldNotBeFailureOfType() = this shouldNot beFailureOfType<A>()
inline fun <reified A : Throwable> beFailureOfType() = object : Matcher<Try<Any>> {
  override fun test(value: Try<Any>): MatcherResult {
    return when (value) {
      is Try.Success<*> -> MatcherResult(false, "Try should be a Failure but was Success(${value.value})", "")
      is Try.Failure -> {
        if (value.exception is A)
          MatcherResult(true, "Try should be a Failure(${A::class})", "Try should not be Failure")
        else
          MatcherResult(false, "Try should be a Failure(${A::class}), but was Failure(${value.exception::class})", "Try should not be Failure")
      }
    }
  }
}
