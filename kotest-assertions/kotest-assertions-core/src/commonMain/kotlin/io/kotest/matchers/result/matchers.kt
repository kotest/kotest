package io.kotest.matchers.result

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.reflect.KClass

fun <T> Result<T>.shouldBeSuccess(block: ((T?) -> Unit)? = null) {
   this should BeSuccess()
   block?.invoke(getOrNull())
}

fun <T> Result<T>.shouldNotBeSuccess() = this shouldNot BeSuccess()

infix fun <T> Result<T>.shouldBeSuccess(expected: T) = this should BeSuccess(expected)
infix fun <T> Result<T>.shouldNotBeSuccess(expected: T) = this shouldNot BeSuccess(expected)

fun Result<Any?>.shouldBeFailure(block: ((Throwable?) -> Unit)? = null) {
   this should BeFailure()
   block?.invoke(this.exceptionOrNull()!!)
}

fun <T> Result<T>.shouldNotBeFailure() = this shouldNot BeFailure()

inline fun <reified A : Throwable> Result<Any?>.shouldBeFailureOfType() = this should BeFailureOfType(A::class)
inline fun <reified A : Throwable> Result<Any?>.shouldNotBeFailureOfType() = this shouldNot BeFailureOfType(A::class)

class BeSuccess<T>(val expected: T? = null) : Matcher<Result<T>> {
   override fun test(value: Result<T>): MatcherResult {
      return when {
         !value.isSuccess -> defaultResult(false)
         expected == null -> defaultResult(true)
         else -> MatcherResult(
            value.getOrNull() == expected,
            "Result should be a Success($expected), but instead got Succes(${value.getOrNull()}).",
            "Result should not be a Success($expected)"
         )
      }
   }

   private fun defaultResult(passed: Boolean) =
      MatcherResult(passed, "Result should be a success.", "Result should not be a success")
}

class BeFailure : Matcher<Result<Any?>> {
   override fun test(value: Result<Any?>) = MatcherResult(
      value.isFailure,
      "Result should be a failure but was ${value.getOrNull()}",
      "Result should not be a failure"
   )
}

class BeFailureOfType<A : Throwable>(private val clazz: KClass<A>) : Matcher<Result<Any?>> {
   override fun test(value: Result<Any?>): MatcherResult {
      val error = value.exceptionOrNull()
      return when {
         value.isSuccess -> MatcherResult(false, "Result should be a failure but was success", "")
         clazz.isInstance(error) -> MatcherResult(
            true,
            "Result should be a Failure($clazz)",
            "Result should not be a Failure($clazz)"
         )
         else -> {
            MatcherResult(false, "Result should be a Failure($clazz) but was Failure(${error!!::class})", "")
         }
      }
   }
}
