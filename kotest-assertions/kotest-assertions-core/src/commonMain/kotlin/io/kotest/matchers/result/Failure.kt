package io.kotest.matchers.result

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.reflect.KClass

fun Result<Any?>.shouldBeFailure(block: ((Throwable) -> Unit)? = null): Throwable {
   this should BeFailure()
   return exceptionOrNull()!!.also { block?.invoke(it) }
}

fun <T> Result<T>.shouldNotBeFailure() = this shouldNot BeFailure()

inline fun <reified A : Throwable> Result<Any?>.shouldBeFailureOfType(): A {
   this should BeFailureOfType(A::class)
   return exceptionOrNull() as A
}

inline fun <reified A : Throwable> Result<Any?>.shouldNotBeFailureOfType() = this shouldNot BeFailureOfType(A::class)
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
