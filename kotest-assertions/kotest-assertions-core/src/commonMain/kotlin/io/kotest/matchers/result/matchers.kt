package io.kotest.matchers.result

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.reflect.KClass

fun <T> Result<T>.shouldBeSuccess(block: ((T) -> Unit)? = null) {
   this should beSuccess()
   if (block != null)
      fold({ block(it) }, {})
}

fun <T> Result<T>.shouldNotBeSuccess() = this shouldNot beSuccess()

infix fun <T> Result<T>.shouldBeSuccess(expected: T) = this should beSuccess(expected)
infix fun <T> Result<T>.shouldNotBeSuccess(expected: T) = this shouldNot beSuccess(expected)

fun Result<Any?>.shouldBeFailure(block: ((Throwable) -> Unit)? = null) {
   this should BeFailure()
   block?.invoke(this.exceptionOrNull()!!)
}

fun <T> Result<T>.shouldNotBeFailure() = this shouldNot BeFailure()

inline fun <reified A : Throwable> Result<Any?>.shouldBeFailureOfType() = this should BeFailureOfType(A::class)
inline fun <reified A : Throwable> Result<Any?>.shouldNotBeFailureOfType() = this shouldNot BeFailureOfType(A::class)

fun <T> beSuccess(): Matcher<Result<T>> = object : Matcher<Result<T>> {
   override fun test(value: Result<T>): MatcherResult {
      return MatcherResult(
         value.isSuccess,
         { "Result should be a Success but was $value" },
         { "Result should not be a Success" })
   }
}

fun <T> beSuccess(expected: T?): BeSuccess<T> = BeSuccess(expected)
class BeSuccess<T>(val expected: T?) : Matcher<Result<T>> {
   override fun test(value: Result<T>): MatcherResult {
      return value.fold(
         {
            MatcherResult(
               it == expected,
               { "Result should be a Success($expected), but instead got Success($it)." },
               { "Result should not be a Success($expected)" })
         },
         {
            defaultResult(false)
         }
      )
   }

   private fun defaultResult(passed: Boolean) =
      MatcherResult(
         passed,
         { "Result should be a success." },
         { "Result should not be a success" })
}

fun beFailure(): BeFailure = BeFailure()
class BeFailure : Matcher<Result<Any?>> {
   override fun test(value: Result<Any?>) = MatcherResult(
      value.isFailure,
      { "Result should be a failure but was ${value.getOrNull()}" },
      { "Result should not be a failure" })
}

class BeFailureOfType<A : Throwable>(private val clazz: KClass<A>) : Matcher<Result<Any?>> {
   override fun test(value: Result<Any?>): MatcherResult {
      val error = value.exceptionOrNull()
      return when {
         value.isSuccess -> MatcherResult(
            false,
            { "Result should be a failure but was success" },
            { "" })
         clazz.isInstance(error) -> MatcherResult(
            true,
            { "Result should be a Failure($clazz)" },
            { "Result should not be a Failure($clazz)" })
         else -> {
            MatcherResult(
               false,
               { "Result should be a Failure($clazz) but was Failure(${error!!::class})" },
               { "" })
         }
      }
   }
}
