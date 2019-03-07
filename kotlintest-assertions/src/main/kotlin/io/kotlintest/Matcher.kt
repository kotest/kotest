package io.kotlintest

/**
 * A [Matcher] is the main abstraction in the assertions library.
 *
 * Implementations contain a single function, called 'test', which
 * accepts a value of type T and returns an instance of [Result].
 * This [Result] return value contains the state of the assertion
 * after it has been evaluted.
 *
 * A matcher will typically be invoked when used with the `should`
 * functions in the assertions DSL. For example, `2 should beLessThan(4)`
 *
 */
interface Matcher<T> {

  fun test(value: T): Result

  fun <U> contramap(f: (U) -> T): Matcher<U> = object : Matcher<U> {
    override fun test(value: U): Result = this@Matcher.test(f(value))
  }

  fun invert(): Matcher<T> = object : Matcher<T> {
    override fun test(value: T): Result {
      val result = this@Matcher.test(value)
      return Result(!result.passed, result.negatedFailureMessage, result.failureMessage)
    }
  }

  infix fun <U> compose(fn: (U) -> T): Matcher<U> = object : Matcher<U> {
    override fun test(value: U): Result = this@Matcher.test(fn(value))
  }

  infix fun and(other: Matcher<T>): Matcher<T> = object : Matcher<T> {
    override fun test(value: T): Result {
      val r = this@Matcher.test(value)
      return if (!r.passed)
        r
      else
        other.test(value)
    }
  }

  infix fun or(other: Matcher<T>): Matcher<T> = object : Matcher<T> {
    override fun test(value: T): Result {
      val r = this@Matcher.test(value)
      return if (r.passed)
        r
      else
        other.test(value)
    }
  }
}

/**
 * A [Matcher] that asserts that the value is not `null` before performing the test.
 *
 * The matcher returned by [invert] will _also_ assert that the value is not `null`. Use this for matchers that
 * should fail on `null` values, whether called with `should` or `shouldNot`.
 */
internal abstract class NeverNullMatcher<T : Any> : Matcher<T?> {
  final override fun test(value: T?): Result {
    return if (value == null) Result(false, "Expecting actual not to be null", "")
    else testNotNull(value)
  }

  override fun invert(): Matcher<T?> = object : NeverNullMatcher<T>() {
    override fun testNotNull(value: T): Result {
      val result = this@NeverNullMatcher.testNotNull(value)
      return Result(!result.passed, result.negatedFailureMessage, result.failureMessage)
    }
  }

  abstract fun testNotNull(value: T): Result
}

internal inline fun <T : Any> neverNullMatcher(crossinline test: (T) -> Result): Matcher<T?> {
  return object : NeverNullMatcher<T>() {
    override fun testNotNull(value: T): Result {
      return test(value)
    }
  }
}

/**
 * The [Result] class contains the result of an evaluation of a matcher.
 *
 * @param passed set to true if the matcher indicated this was a valid
 * value and false if the matcher indicated an invalid value
 *
 * @param failureMessage a message indicating why the evaluation failed
 * for when this matcher is used in the positive sense. For example,
 * if a size matcher was used like `mylist should haveSize(5)` then
 * an appropriate error message would be "list should be size 5".
 *
 * @param negatedFailureMessage a message indicating why the evaluation
 * failed for when this matcher is used in the negative sense. For example,
 * if a size matcher was used like `mylist shouldNot haveSize(5)` then
 * an appropriate negated failure would be "List should not have size 5".
 */
data class Result(val passed: Boolean, val failureMessage: String, val negatedFailureMessage: String)
