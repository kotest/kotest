package io.kotest.matchers.result

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

/**
 * Asserts that this result is not a Failure
 *
 * success("abc").shouldNotBeFailure()       // Assertion passes
 * failure(MyException).shouldNotBeFailure() // Assertion fails
 *
 * @see shouldBeSuccess
 */
@Deprecated("Use shouldBeSuccess instead. Deprecated since 6.0", ReplaceWith("shouldBeSuccess()"))
fun <T> Result<T>.shouldNotBeFailure(): T = shouldBeSuccess()

/**
 * Asserts that this result is any Failure
 *
 * failure(MyException).shouldBeFailure()  // Assertion passes
 * success("abc").shouldBeFailure()        // Assertion fails
 *
 * @see shouldNotBeSuccess
 */
fun Result<*>.shouldBeFailure(): Throwable {
   this should FailureMatcher()
   return exceptionOrNull()!!
}

/**
 * Asserts that this result fails with exactly [expected]
 *
 * failure(MyException) shouldBeFailure MyException     // Assertion passes
 * failure(OtherException) shouldBeFailure MyException  // Assertion fails
 * success("abc") shouldBeFailure MyException           // Assertion fails
 *
 */
infix fun Result<*>.shouldBeFailure(expected: Throwable): Throwable {
   this should FailureMatcher(expected)
   return exceptionOrNull()!!
}

/**
 * Asserts that this result is a failure, and lets you use its error in [block]
 *
 * failure(MyException) shouldBeFailure {
 *   it shouldHaveMessage "MyException"
 * }
 */
infix fun Result<*>.shouldBeFailure(block: ((Throwable) -> Unit)): Throwable {
   this should FailureMatcher()
   return exceptionOrNull()!!.also { block(it) }
}

/**
 * Asserts that this result is a failure of type [T]
 *
 * failure(MyException).shouldBeFailure<MyException>()      // Assertion passes
 * failure(MyException).shouldBeFailure<MyOtherException>() // Assertion fails
 * success("abc").shouldBeFailure<MyException>()            // Assertion fails
 *
 */
@JvmName("shouldBeFailureT")
inline fun <reified T : Throwable> Result<*>.shouldBeFailure(): T {
   this should FailureTypeMatcher(T::class)
   return exceptionOrNull() as T
}

internal val AnyError = object : Throwable() {}

@Suppress("FunctionName")
@JsName("EmptyConstructorFailureMatcher")
fun FailureMatcher(): Matcher<Result<*>> = FailureMatcher(AnyError)
class FailureMatcher<T : Throwable>(val expected: T) : Matcher<Result<*>> {
   override fun test(value: Result<*>): MatcherResult {
      if (value.isSuccess) return MatcherResult(false, { "Expected a Failure, but got $value" }, { "" })

      val actual = value.exceptionOrNull()
      if (expected === AnyError) {
         return MatcherResult(true, { "" }, { "" })
      }

      return MatcherResult(
         actual == expected,
         { "Result should be Failure($expected) but was Failure($actual)" },
         { "Result should not be a failure, but was $actual" }
      )
   }
}

class FailureTypeMatcher<T : Throwable>(val clazz: KClass<T>) : Matcher<Result<*>> {
   override fun test(value: Result<*>): MatcherResult {
      if (value.isSuccess) return MatcherResult(false, { "Expected a Failure, but got $value" }, { "" })

      val error = value.exceptionOrNull()!!

      return MatcherResult(
         clazz.isInstance(error),
         { "Result should be a failure of type $clazz but was ${error::class}" },
         { "" }
      )
   }
}
