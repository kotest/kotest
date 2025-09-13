@file:Suppress("DEPRECATION")

package io.kotest.matchers

import io.kotest.assertions.print.Printed
import kotlin.js.JsName

/**
 * A [Matcher] is the main abstraction in the assertions library.
 *
 * Implementations contain a single function, called 'test', which
 * accepts a value of type T and returns an instance of [MatcherResult].
 * This [MatcherResult] return value contains the state of the assertion
 * after it has been evaluated.
 *
 * A matcher will typically be invoked when used with the `should`
 * functions in the assertions DSL. For example, `2 should beLessThan(4)`
 *
 */
interface Matcher<in T> {

   fun test(value: T): MatcherResult

   /**
    * Returns a [Matcher] which has the inverse logic of this matcher.
    * Eg, if the matcher would fail on size < 10, this matcher would now pass for < 10.
    * The error messages are inverted to match, so the failure message becomes the success message.
    */
   fun invert(): Matcher<T> = Matcher {
      with(test(it)) {
         MatcherResult(!passed(), { negatedFailureMessage() }, { failureMessage() })
      }
   }

   infix fun <U> contramap(f: (U) -> T): Matcher<U> = Matcher { test(f(it)) }

   fun invertIf(invert: Boolean): Matcher<T> = if (invert) invert() else this

   companion object {

      /**
       * Returns a [Matcher] for type T that will always fail with the given [error] message.
       */
      fun <T> failure(error: String) = Matcher<T> { MatcherResult.invoke(false, { error }, { "" }) }

      /**
       * Creates a matcher with the given function to evaluate the value and return a [MatcherResult].
       *
       * @param tester The function that evaluates a value and returns a [MatcherResult].
       */
      inline operator fun <T> invoke(crossinline tester: (T) -> MatcherResult) = object : Matcher<T> {
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
 * An instance of [MatcherResult] contains the result of an evaluation of a [Matcher].
 */
interface MatcherResult {

   /**
    * Returns true if the matcher indicated this was a valid
    * value and false if the matcher indicated an invalid value.
    */
   fun passed(): Boolean

   /**
    * Returns a message indicating why the matcher failed for when this matcher
    * is used in the positive sense. For example, if a size matcher was used
    * like `mylist should haveSize(5)` then an appropriate error message would
    * be "list should be size 5".
    */
   fun failureMessage(): String

   /**
    * Returns a message indicating why the matcher failed for when this matcher
    * is used in the negative sense. For example, if a size matcher was used
    * like `mylist shouldNot haveSize(5)` then an appropriate negated failure
    * would be "List should not have size 5".
    */
   fun negatedFailureMessage(): String

   companion object {
      operator fun invoke(
         passed: Boolean,
         failureMessageFn: () -> String,
         negatedFailureMessageFn: () -> String,
      ): MatcherResult = object : MatcherResult {
         override fun passed(): Boolean = passed
         override fun failureMessage(): String = failureMessageFn()
         override fun negatedFailureMessage(): String = negatedFailureMessageFn()
      }
   }
}

/**
 * An instance of [MatcherResult] that contains the actual and expected values
 * as [Printed] values, along with the failure and negated failure messages.
 *
 * By returning this [MatcherResult], Kotest will automatically generate the appropriate
 * assertion error message that contains the actual and expected values in a way
 * that allows intellij to create a <Click to see difference> link in the IDE output window.
 */
data class ComparisonMatcherResult(
   @JsName("passed_val") val passed: Boolean,
   val actual: Printed,
   val expected: Printed,
   val failureMessageFn: () -> String,
   val negatedFailureMessageFn: () -> String,
) : MatcherResult {
   override fun passed(): Boolean = passed
   override fun failureMessage(): String = failureMessageFn()
   override fun negatedFailureMessage(): String = negatedFailureMessageFn()
}

@Deprecated("Use ComparisonMatcherResult")
interface ComparableMatcherResult : MatcherResult {

   fun actual(): String

   fun expected(): String

   companion object {
      operator fun invoke(
         passed: Boolean,
         failureMessageFn: () -> String,
         negatedFailureMessageFn: () -> String,
         actual: String,
         expected: String,
      ): ComparableMatcherResult = object : ComparableMatcherResult {
         override fun passed(): Boolean = passed
         override fun failureMessage(): String = failureMessageFn()
         override fun negatedFailureMessage(): String = negatedFailureMessageFn()
         override fun actual(): String = actual
         override fun expected(): String = expected
      }
   }
}

@Deprecated("Use ComparisonMatcherResult")
interface EqualityMatcherResult : MatcherResult {

   fun actual(): Any?

   fun expected(): Any?

   companion object {
      operator fun invoke(
         passed: Boolean,
         actual: Any?,
         expected: Any?,
         failureMessageFn: () -> String,
         negatedFailureMessageFn: () -> String,
      ): EqualityMatcherResult = object : EqualityMatcherResult {
         override fun passed(): Boolean = passed
         override fun failureMessage(): String = failureMessageFn()
         override fun negatedFailureMessage(): String = negatedFailureMessageFn()
         override fun actual(): Any? = actual
         override fun expected(): Any? = expected
      }
   }
}
