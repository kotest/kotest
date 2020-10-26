package io.kotest.matchers

/**
 * A [Matcher] is the main abstraction in the assertions library.
 *
 * Implementations contain a single function, called 'test', which
 * accepts a value of type T and returns an instance of [MatcherResult].
 * This [MatcherResult] return value contains the state of the assertion
 * after it has been evaluted.
 *
 * A matcher will typically be invoked when used with the `should`
 * functions in the assertions DSL. For example, `2 should beLessThan(4)`
 *
 */
interface Matcher<in T> {

   fun test(value: T): MatcherResult

   fun <U> contramap(f: (U) -> T): Matcher<U> = object : Matcher<U> {
      override fun test(value: U): MatcherResult = this@Matcher.test(f(value))
   }

   fun invert(): Matcher<T> = object : Matcher<T> {
      override fun test(value: T): MatcherResult {
         val result = this@Matcher.test(value)
         return MatcherResult(!result.passed(), { result.negatedFailureMessage() }, { result.failureMessage() })
      }
   }

   infix fun <U> compose(fn: (U) -> T): Matcher<U> = object : Matcher<U> {
      override fun test(value: U): MatcherResult = this@Matcher.test(fn(value))
   }
}

infix fun <T> Matcher<T>.and(other: Matcher<T>): Matcher<T> = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val r = this@and.test(value)
      return if (!r.passed())
         r
      else
         other.test(value)
   }
}

infix fun <T> Matcher<T>.or(other: Matcher<T>): Matcher<T> = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val r = this@or.test(value)
      return if (r.passed())
         r
      else
         other.test(value)
   }
}

/**
 * A [Matcher] that asserts that the value is not `null` before performing the test.
 *
 * The matcher returned by [invert] will _also_ assert that the value is not `null`. Use this for matchers that
 * should fail on `null` values, whether called with `should` or `shouldNot`.
 */
internal abstract class NeverNullMatcher<T : Any> : Matcher<T?> {
   final override fun test(value: T?): MatcherResult {
      return if (value == null) MatcherResult(false, "Expecting actual not to be null", "")
      else testNotNull(value)
   }

   override fun invert(): Matcher<T?> = object : NeverNullMatcher<T>() {
      override fun testNotNull(value: T): MatcherResult {
         val result = this@NeverNullMatcher.testNotNull(value)
         return MatcherResult(!result.passed(), result.negatedFailureMessage(), result.failureMessage())
      }
   }

   abstract fun testNotNull(value: T): MatcherResult
}

fun <T : Any> neverNullMatcher(test: (T) -> MatcherResult): Matcher<T?> {
   return object : NeverNullMatcher<T>() {
      override fun testNotNull(value: T): MatcherResult {
         return test(value)
      }
   }
}

/**
 * An instance of [MatcherResult] contains the result of an evaluation of a [Matcher].
 */
interface MatcherResult {

   /**
    * Returns true if the matcher indicated this was a valid
    * value and false if the matcher indicated an invalid value.
    */
   fun passed(): Boolean

   /**
    * Returns a message indicating why the evaluation failed
    * for when this matcher is used in the positive sense. For example,
    * if a size matcher was used like `mylist should haveSize(5)` then
    * an appropriate error message would be "list should be size 5".
    */
   fun failureMessage(): String

   /**
    * Returns a message indicating why the evaluation
    * failed for when this matcher is used in the negative sense. For example,
    * if a size matcher was used like `mylist shouldNot haveSize(5)` then
    * an appropriate negated failure would be "List should not have size 5".
    */
   fun negatedFailureMessage(): String

   companion object {

      operator fun invoke(
         passed: Boolean,
         failureMessage: String,
         negatedFailureMessage: String
      ) = object : MatcherResult {
         override fun passed(): Boolean = passed
         override fun failureMessage(): String = failureMessage
         override fun negatedFailureMessage(): String = negatedFailureMessage
      }

      operator fun invoke(
         passed: Boolean,
         failureMessageFn: () -> String,
         negatedFailureMessageFn: () -> String
      ) = object : MatcherResult {
         override fun passed(): Boolean = passed
         override fun failureMessage(): String = failureMessageFn()
         override fun negatedFailureMessage(): String = negatedFailureMessageFn()
      }
   }
}
