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

   fun <U> contramap(f: (U) -> T): Matcher<U> = Matcher { this@Matcher.test(f(it)) }

   fun invert(): Matcher<T> = Matcher {
      with(test(it)) {
         MatcherResult(!passed(), { negatedFailureMessage() }, { failureMessage() })
      }
   }

   infix fun <U> compose(fn: (U) -> T): Matcher<U> = Matcher { this@Matcher.test(fn(it)) }

   companion object {
      /**
       * Returns a [Matcher] for type T that will always fail with the given [error] message.
       */
      fun <T> failure(error: String) = Matcher<T> { MatcherResult(false, "", error) }

      /**
       * Create matcher with the given function to evaluate the value and return a MatcherResult
       *
       * @param tester The function that evaluates a value and returns a MatcherResult
       */
      inline operator fun <T> invoke(crossinline tester: (T) -> MatcherResult) = object: Matcher<T> {
         override fun test(value: T) = tester(value)
      }
   }
}

infix fun <T> Matcher<T>.and(other: Matcher<T>): Matcher<T> = Matcher {
   test(it)
      .takeUnless(MatcherResult::passed)
      ?: other.test(it)
}

infix fun <T> Matcher<T>.or(other: Matcher<T>): Matcher<T> = Matcher {
   test(it)
      .takeIf(MatcherResult::passed)
      ?: other.test(it)
}

/**
 * A [Matcher] that asserts that the value is not `null` before performing the test.
 *
 * The matcher returned by [invert] will _also_ assert that the value is not `null`. Use this for matchers that
 * should fail on `null` values, whether called with `should` or `shouldNot`.
 */
internal abstract class NeverNullMatcher<T : Any?> : Matcher<T?> {
   final override fun test(value: T?): MatcherResult {
      return if (value == null) MatcherResult(false, "Expecting actual not to be null", "")
      else testNotNull(value)
   }

   override fun invert(): Matcher<T?> = object : NeverNullMatcher<T?>() {
      override fun testNotNull(value: T?): MatcherResult {
         if (value == null) return MatcherResult(false, "Expecting actual not to be null", "")
         val result = this@NeverNullMatcher.testNotNull(value)
         return MatcherResult(!result.passed(), result.negatedFailureMessage(), result.failureMessage())
      }
   }

   abstract fun testNotNull(value: T): MatcherResult

   companion object {
      /**
       * Create matcher with the given function to evaluate the value and return a MatcherResult
       *
       * @param tester The function that evaluates a value and returns a MatcherResult
       */
      inline operator fun <T: Any> invoke(crossinline tester: (T) -> MatcherResult) = object: NeverNullMatcher<T>() {
         override fun testNotNull(value: T) = tester(value)
      }
   }
}

fun <T : Any> neverNullMatcher(t: (T) -> MatcherResult): Matcher<T?> {
   return object : NeverNullMatcher<T>() {
      override fun testNotNull(value: T): MatcherResult {
         return t(value)
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
