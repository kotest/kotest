package io.kotest.matchers.result

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Verifies that this result is not a success
 *
 * failure(MyException).shouldNotBeSuccess() // Assertion passes
 * success("abc").shouldNotBeSuccess()       // Assertion fails
 *
 * @see [shouldBeFailure]
 */
fun <T> Result<T>.shouldNotBeSuccess() = this shouldNot beSuccess()

/**
 * Verifies that this result is any success
 *
 * success("abc").shouldBeSuccess()       // Assertion passes
 * failure(MyException).shouldBeSuccess() // Assertion fails
 */
fun <T> Result<T>.shouldBeSuccess(): T {
  this should beSuccess()
  return getOrThrow()
}

/**
 * Verifies that this result is a success containing [expected]
 *
 * success("abc") shouldBeSuccess "abc"       // Assertion passes
 * success("abc") shouldBeSuccess "cba"       // Assertion fails
 * failure(MyException) shouldBeSuccess "abc" // Assertion fails
 */
infix fun <T> Result<T>.shouldBeSuccess(expected: T): T {
  this should beSuccess(expected)
  return getOrThrow()
}

/**
 * Verifies that this result is a success and lets you use its value in [block]
 *
 * success("abc") shouldBeSuccess { value: String ->
 *    value shouldStartWith "a"
 * }
 *
 * failure(MyException) shouldBeSuccess { value: String ->
 *   // Never gets called, as result was a failure
 * }
 *
 */
infix fun <T> Result<T>.shouldBeSuccess(block: ((T) -> Unit)): T {
  this should beSuccess()
  return getOrThrow().also { block(it) }
}

fun <T> beSuccess(): Matcher<Result<T?>> = beSuccess(Unit)

fun <T> beSuccess(expected: T?): Matcher<Result<T?>> = SuccessMatcher(expected)

class SuccessMatcher<T>(val expected: T?) : Matcher<Result<T?>> {
  override fun test(value: Result<T?>): MatcherResult {
    if (value.isFailure) return MatcherResult(false, { "Expected to assert on a Success, but was $value" }, { "" })
    val actual = value.getOrThrow()

    if (expected == Unit) return MatcherResult(true, { "" }, { "" })

    return MatcherResult(
      actual == expected,
      { "Result should be Success($expected), but was Success($actual)" },
      { "Result should not be a Success, but was Success($actual)" }
    )
  }
}

@Deprecated("Use SuccessMatcher instead", ReplaceWith("SuccessMatcher"))
typealias BeSuccess<T> = SuccessMatcher<T>

@Deprecated("The use of this function is not clear in naming and should be avoided")
infix fun <T> Result<T>.shouldNotBeSuccess(expected: T) = this shouldNot beSuccess(expected)
