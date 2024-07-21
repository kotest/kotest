package io.kotest.matchers.properties

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.eq.eq
import io.kotest.assertions.intellijFormatError
import io.kotest.assertions.print.print
import io.kotest.assertions.withClue
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import kotlin.reflect.KProperty0

/**
 * Asserts that this property has a specific value. Unlike regular [shouldBe], the name of the property
 * will be automatically added to the error message
 */
infix fun <T> KProperty0<T>.shouldHaveValue(expected: T) = this should haveValue(expected)

/**
 * Asserts that this property does not have a specific value. Unlike regular [shouldNotBe], the name of the
 * property will be automatically added to the error message
 */
infix fun <T> KProperty0<T>.shouldNotHaveValue(expected: T) = this shouldNot haveValue(expected)

fun <T> haveValue(expected: T) = object : Matcher<KProperty0<T>> {
   override fun test(value: KProperty0<T>): MatcherResult {
      val prependMessage = { "Assertion failed for property '${value.name}'" }
      val actual = value.get()
      return object : MatcherResult {
         override fun passed(): Boolean =
            eq(actual, expected) == null

         override fun failureMessage(): String =
            prependMessage() + "\n" + intellijFormatError(Expected(expected.print()), Actual(actual.print()))

         override fun negatedFailureMessage(): String =
            prependMessage() + "\n${expected.print().value} should not equal ${actual.print().value}"
      }
   }
}

/**
 * Perform assertions on the value of this property.
 *
 * Name of the property will be automatically added to the error message should any failures occur within the block.
 */
inline infix fun <T> KProperty0<T>.shouldMatch(block: T.() -> Unit) {
   withClue(name) {
      block(get())
   }
}
