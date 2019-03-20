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

